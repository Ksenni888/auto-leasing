package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.ContractDto;
import ru.bell.Exceptions.CarNotFoundException;
import ru.bell.Exceptions.ClientNotFoundException;
import ru.bell.Exceptions.ContractNotFoundException;
import ru.bell.Exceptions.DataConflictException;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Interfaces.CarService;
import ru.bell.Interfaces.ContractService;
import ru.bell.Mapper.CarMapper;
import ru.bell.Mapper.ContractMapper;
import ru.bell.Mapper.PaymentMapper;
import ru.bell.Model.Car;
import ru.bell.Model.Contract;
import ru.bell.Model.Payment;
import ru.bell.Repository.ClientRepository;
import ru.bell.Repository.ContractRepository;
import ru.bell.ServiceKafka.KafkaProducerService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private final CarService carService;
    private final ClientRepository clientRepository;

    String PATH = "leasingContracts.txt";

    @Override
    @Transactional
    public ContractDto createContract(ContractDto contractDto) {

        Car car = CarMapper.toCar(carService.getCarByVin(contractDto.getCarVIN()));
        checkInputDate(contractDto, car);

        BigDecimal amountOfFinancing = car.getCost().subtract(contractDto.getInitialPayment());
        if (amountOfFinancing.compareTo(contractDto.getInitialPayment())<0) {
            log.warn("Первый платеж не может быть больше стоимости машины");
            throw new InvalidRequestException("Первый платеж не может быть больше стоимости машины");
        }
        contractDto.setAmountOfFinancing(amountOfFinancing);
        contractDto.setClosed(false);

        Long contractId = kafkaProducerService.sendContractMessageAndWait("CREATE", contractDto);

        carService.checkCarAvailable(car, false);
        BigDecimal amountForPayment = paymentСalculation(contractDto.getPeriod(), contractDto.getPercent(), amountOfFinancing);
        List<Payment> payments = createPaymentSchedule(contractDto.getPeriod(), amountForPayment, contractId);

        contractDto.setId(contractId);
        log.info("Contract creation request sent to queue for: {}", contractDto.getId());
        log.info("Договор создан");
        List<Long> paymentsIds = payments.stream().map(x -> x.getPaymentId()).toList();
        Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new ContractNotFoundException("Такого контракта нет"));

        contractToFile(contract, paymentsIds);
        return ContractMapper.toContractDto(contract);

    }

    @Override
    @Transactional
    public ContractDto closeContracts(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        contract.setClosed(true);
        ContractDto contractDto = ContractMapper.toContractDto(contract);
        kafkaProducerService.sendContractMessage("CLOSE", contractDto);
        return contractDto;
    }

    @Transactional
    public List<Payment> createPaymentSchedule(Integer period, BigDecimal amountForPayment, long contractId) {
        List<Payment> paymentsList = new ArrayList<>();
        for (int i = 0; i < period; i++) {
            long id = i+1;
            Payment payment = new Payment();
            payment.setPaymentId(id);
            payment.setContractId(contractId);
            payment.setAmount(amountForPayment);
            payment.setPaid(false);
            paymentsList.add(payment);

            kafkaProducerService.sendPaymentMessage("CREATE", PaymentMapper.toDto(payment));
        }
        log.info("График платежей создан");
        return paymentsList;
    }

    @Override
    public List<ContractDto> activeContracts() {
        List<Contract> result = contractRepository.findByIsClosedFalse(false);
        result.sort(Comparator.comparing(Contract::getId));
        return result.stream().map(x -> ContractMapper.toContractDto(x)).toList();
    }

    @Override
    public List<ContractDto> findAll() {
        return contractRepository.findAll(Sort.by("id").ascending()).stream().map(x -> ContractMapper.toContractDto(x)).toList();
    }

    @Override
    public ContractDto findContractId(Long id) {
        return ContractMapper.toContractDto(contractRepository.findById(id).orElseThrow(()-> new ContractNotFoundException("Контракта нет в базе")));
    }

    @Override
    public List<ContractDto> findContractsByClientId(Long id) {
        List<Contract> result = contractRepository.findContractsByClientId(id);
        result.sort(Comparator.comparing(Contract::getId));
        return result.stream().map(x->ContractMapper.toContractDto(x)).toList();
    }

    public void checkInputDate(ContractDto contractDto, Car car){
        if (contractDto.getId() != null) {
            log.warn("Поле с id должно быть пустым");
            throw new InvalidRequestException("Поле с id должно быть пустым");}
        if (car == null) {
            log.warn("Машины с VIN " + contractDto.getCarVIN() + " в базе нет");
            throw new CarNotFoundException("Машины с VIN " + contractDto.getCarVIN() + " в базе нет");
        }
        if (!car.isAvailable()) {
            log.warn("Машина с VIN " + contractDto.getCarVIN() + " в лизинге");
            throw new DataConflictException("Машина с VIN " + contractDto.getCarVIN() + " в лизинге"); }

        clientRepository.findById(contractDto.getClientId()).orElseThrow(() -> new ClientNotFoundException("Такого клиента в базе нет"));
        log.warn("Такого клиента в базе нет");
    }

    public BigDecimal paymentСalculation(Integer period, BigDecimal percent, BigDecimal amountOfFinancing) {
        try {
            BigDecimal M = (percent.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))
                    .divide(new BigDecimal(12), 4, RoundingMode.HALF_UP);
            BigDecimal x = (new BigDecimal(1).add(M)).pow(period);
            BigDecimal y = ((new BigDecimal(1).add(M)).pow(period)).subtract(new BigDecimal(1));
            return amountOfFinancing.multiply((M.multiply(x)).divide(y, 4, RoundingMode.HALF_UP));
        } catch (ArithmeticException e) {
            log.error("/by zero", e);
        }
        return null;
    }

    public void contractToFile(Contract contract, List<Long> paymentSchedule){
        try {
            File file = new File(PATH);
            FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(String.valueOf(contract.getId())).append(",");
            bufferedWriter.append(contract.getCarVIN()).append(",");
            bufferedWriter.append(String.valueOf(contract.getClientId())).append(",");
            bufferedWriter.append(String.valueOf(contract.getPeriod())).append(",");
            bufferedWriter.append(String.valueOf(contract.getInitialPayment())).append(",");
            bufferedWriter.append(String.valueOf(contract.getPercent())).append(",");
            bufferedWriter.append("false").append(",");
            bufferedWriter.append(String.valueOf(contract.getAmountOfFinancing())).append(",");
            bufferedWriter.append(paymentSchedule.toString().substring(1, paymentSchedule.toString().length() - 1));
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Ошибка записи в файл " + PATH);
            log.error("Ошибка записи в файл " + PATH, e);
        }
    }
}
