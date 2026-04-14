package ru.bell.Mapper;

import ru.bell.Dto.PaymentDto;
import ru.bell.Model.Payment;
import ru.bell.Model.PaymentId;

public class PaymentMapper {

    public static Payment toPayment(PaymentDto dto) {
        PaymentId paymentId = PaymentId.builder()
                .contractId(dto.getContractId())
                .paymentId(dto.getId())
                .build();

        return Payment.builder()
                .paymentId(paymentId.getPaymentId())
                .contractId(paymentId.getContractId())
                .amount(dto.getAmount())
                .paid(dto.isPaid())
                .build();
    }

    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .contractId(payment.getContractId())
                .id(payment.getPaymentId())
                .amount(payment.getAmount())
                .paid(payment.isPaid())
                .build();
    }
}
