package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Payment;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Lấy Role của người đang thao tác
    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            Account acc = accountRepository.findByUsername(auth.getName());
            if (acc != null) return acc.getRole();
        }
        return "Nhân viên";
    }

    public Page<Payment> getPaymentsPaged(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return paymentRepository.searchPayments(keyword, status, pageable);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findAllByStatus(status);
    }

    public List<Payment> getPaymentsByContractId(Integer contractId) {
        return paymentRepository.findByContractId(contractId);
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    // ===============================================
    // 1. TẠO THANH TOÁN (ÉP CHỜ DUYỆT NẾU LÀ NHÂN VIÊN)
    // ===============================================
    public Payment createPayment(Payment payment) {
        Contract contract = contractRepository.findById(payment.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + payment.getContractId()));
        payment.setContract(contract);

        if ("Nhân viên".equals(getCurrentUserRole())) {
            payment.setStatus("Chờ duyệt");
        }

        return paymentRepository.save(payment);
    }

    // ===============================================
    // 2. CẬP NHẬT THANH TOÁN
    // ===============================================
    public Payment updatePayment(Integer id, Payment payment) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        Contract contract = contractRepository.findById(payment.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + payment.getContractId()));
        existingPayment.setContract(contract);

        if (payment.getAmount() != null) {
            existingPayment.setAmount(payment.getAmount());
        }
        if (payment.getPaymentDate() != null) {
            existingPayment.setPaymentDate(payment.getPaymentDate());
        }
        if (payment.getMethod() != null) {
            existingPayment.setMethod(payment.getMethod());
        }

        // Logic trạng thái
        if ("Nhân viên".equals(getCurrentUserRole())) {
            existingPayment.setStatus("Chờ duyệt"); // Nhân viên sửa thì tự tụt về chờ duyệt
        } else if (payment.getStatus() != null) {
            existingPayment.setStatus(payment.getStatus());
        }

        return paymentRepository.save(existingPayment);
    }

    // ===============================================
    // 3. DUYỆT NHANH TRẠNG THÁI
    // ===============================================
    public Payment updateStatus(Integer id, String newStatus) {
        Payment payment = getPaymentById(id);
        payment.setStatus(newStatus);
        return paymentRepository.save(payment);
    }

    // ĐÃ XÓA HÀM deletePaymentById

    public double getTotalPaymentsThisMonth() {
        return paymentRepository.calculateTotalPaymentsThisMonth();
    }

    public List<Object[]> getRevenueByMonth() {
        return paymentRepository.getRevenueByMonth();
    }

    public List<Object[]> getRecentPayments() {
        return paymentRepository.findRecentPayments();
    }
}