package ru.bell.Interfaces;

import ru.bell.Dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(PaymentDto paymentDto);
    List<PaymentDto> findAllPaymentsByContractId(Long id);
}
