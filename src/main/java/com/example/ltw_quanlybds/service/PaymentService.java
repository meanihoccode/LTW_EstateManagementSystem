package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Payment;
import com.example.ltw_quanlybds.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional

public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayments()
    {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByStatus(String status)
    {
        return paymentRepository.findAllByStatus(status);
    }

    public List<Payment> getPaymentsByContractId(Integer contractId)
    {
        return paymentRepository.findByContractId(contractId);
    }

    public Payment getPaymentById(Integer id)
    {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deletePaymentById(Integer id)
    {
        paymentRepository.deleteById(id);
    }
    public Payment updatePayment(Integer id, Payment payment) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        existingPayment.setAmount(payment.getAmount());
        existingPayment.setPaymentDate(payment.getPaymentDate());
        existingPayment.setStatus(payment.getStatus());
        existingPayment.setMethod(payment.getMethod());
        return paymentRepository.save(existingPayment);
    }
}