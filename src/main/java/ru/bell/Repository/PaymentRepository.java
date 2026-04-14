package ru.bell.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bell.Model.Payment;
import ru.bell.Model.PaymentId;

import java.util.List;

@Repository
    public interface PaymentRepository extends JpaRepository<Payment, PaymentId> {
       List<Payment> findByContractId(Long contractId);
    }