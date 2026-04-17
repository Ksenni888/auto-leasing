//package ru.bell;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import ru.bell.Controller.PaymentManagement;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Scanner;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class PaymentManagementTest {
//    PaymentManagement paymentManagement = new PaymentManagement();
//
//    @Test
//    void createPaymentScheduleTest(){
//        int period = 4;
//        BigDecimal result = new BigDecimal("12000.90");
//        int contractId = 2;
//        List<Integer> paymentsIds = paymentManagement.createPaymentSchedule(period,result,contractId);
//        assertEquals(paymentsIds.size(), 4);
//        assertEquals(paymentsIds.get(0), 1);
//    }
//
//    @Test
//    void checkBigDecimalNumberTest(){
//        Scanner mockScanner = Mockito.mock(Scanner.class);
//        paymentManagement.setScanner(mockScanner);
//        Mockito.when(mockScanner.hasNextBigDecimal()).thenReturn(true);
//        Mockito.when(mockScanner.nextBigDecimal()).thenReturn(new BigDecimal("1200.30"));
//
//        assertEquals(paymentManagement.checkBigDecimalNumber(), new BigDecimal("1200.30"));
//    }
//}