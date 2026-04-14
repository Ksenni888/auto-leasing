package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.PaymentDto;
import ru.bell.Exceptions.DataConflictException;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Interfaces.CarService;
import ru.bell.Interfaces.ContractService;
import ru.bell.Interfaces.PaymentService;
import ru.bell.Mapper.CarMapper;
import ru.bell.Mapper.PaymentMapper;
import ru.bell.Model.Payment;
import ru.bell.Repository.PaymentRepository;
import ru.bell.ServiceKafka.KafkaProducerService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImp implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ContractService contractService;
    private final CarService carService;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto) {
        List<Payment> paymentsList = findAllPaymentsByContractId(paymentDto.getContractId()).stream()
                .map(x -> PaymentMapper.toPayment(x)).toList();
        int notPaidPayments = paymentsList.stream().filter(x -> !x.isPaid()).toList().size();
        checkInputDate(paymentDto, notPaidPayments);
        for (Payment i: paymentsList) {
            if (!i.isPaid()) {
                BigDecimal amount = i.getAmount();
                if (paymentDto.getAmount().equals(amount)) {
                    paymentDto.setPaid(true);
                    paymentDto.setId(i.getPaymentId());
                    kafkaProducerService.sendPaymentMessage("UPDATE", paymentDto);
                    log.info("Запрос отправлен в очередь, создание платежа id : {}", paymentDto.getId());

                    if (notPaidPayments-1==0)
                    {contractService.closeContracts(paymentDto.getContractId());
                        log.info("Договор закрыт");
                        carService.checkCarAvailable(CarMapper.toCar(carService.getCarByVin(
                                contractService.findContractId(i.getContractId()).getCarVIN())),true);
                    }
                    break;
                } else {
                    log.warn("Сумма должня быть " + amount);
                    throw new InvalidRequestException("Сумма должня быть "+amount);}
            }
        }
        return paymentDto;
    }

    public void checkInputDate(PaymentDto paymentDto, int notPaidPayments){
        if (paymentDto.getId() != null) {
            log.warn("Поле с номером платежа должно быть пустым");
            throw new InvalidRequestException("Поле с номером платежа должно быть пустым"); }
        if (paymentDto.getContractId() == null) {
            log.warn("Поле с номером договора не может быть пустым");
            throw new InvalidRequestException("Поле с номером договора не может быть пустым"); }
        if (paymentDto.isPaid()) {
            log.warn("Поле со статусом платежа должно быть пустым");
            throw new InvalidRequestException("Поле со статусом платежа должно быть пустым");}
        if (notPaidPayments == 0) {
            log.warn("Договор уже закрыт.");
            throw new DataConflictException("Договор закрыт.");}
        if (contractService.findContractId(paymentDto.getContractId()).isClosed()) {
            throw new DataConflictException("Договор закрыт.");
        }
    }

    @Override
    public List<PaymentDto> findAllPaymentsByContractId(Long id) {
        List<Payment> result = paymentRepository.findByContractId(id);
        result.sort(Comparator.comparing(Payment::getPaymentId));
        return result.stream().map(x -> PaymentMapper.toDto(x)).toList();
    }
}