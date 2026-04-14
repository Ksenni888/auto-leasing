package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Interfaces.ContractService;
import ru.bell.Interfaces.PaymentService;
import ru.bell.Mapper.ContractMapper;
import ru.bell.Model.Contract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DateConflict {
    String PATH = "leasingContracts.txt";
    private final ContractService contractService;
    private final PaymentService paymentService;
    List<Contract> contracts = new ArrayList<>();

    public void contractsFromFile() {
        try {
            File file = new File(PATH);
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = bufferedReader.readLine();

            while ((s != null)&&(!s.isEmpty())) {
                List<Long> res = new ArrayList<>();
                String[] str = s.split(",");
                for (int i = 8; i<str.length; i++){
                    res.add(Long.parseLong(str[i].trim()));
                }
                Contract leasingContract = new Contract();
                leasingContract.setId(Long.parseLong(str[0]));
                leasingContract.setCarVIN(str[1]);
                leasingContract.setClientId(Long.valueOf(str[2]));
                leasingContract.setPeriod(Integer.valueOf(str[3]));
                leasingContract.setInitialPayment(new BigDecimal(str[4]));
                leasingContract.setPercent(new BigDecimal(str[5]));
                leasingContract.setClosed(Boolean.parseBoolean(str[6]));
                leasingContract.setAmountOfFinancing(new BigDecimal(str[7]));
                leasingContract.setPayments(res);
                contracts.add(leasingContract);
                s = bufferedReader.readLine();
            }
            bufferedReader.close();

        } catch (Exception e) {
            System.out.println("Невозможно прочитать файл " + PATH);
            log.error("Невозможно прочитать файл " + PATH, e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void checkConflict(){
        File file = new File(PATH);
        File file1 = new File("leasingContracts1.txt");
        List <Contract> contractsFromBD = contractService.findAll().stream().map(x -> ContractMapper.toContract(x)).toList();
        contractsFromFile();
        for (int i=0; i < contractsFromBD.size(); i++){
            if (contracts.isEmpty() || (contracts.get(i) != contractsFromBD.get(i))) {
                file.delete();
                file1.renameTo(new File("leasingContracts.txt"));
                for (Contract c: contractsFromBD){
                    contractService.contractToFile(c, paymentService.findAllPaymentsByContractId(c.getId()).stream().map(x -> x.getId()).toList());
                }
                break;
            }
        }
    }
}
