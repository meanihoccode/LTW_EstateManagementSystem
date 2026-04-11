package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Payment;
import com.example.ltw_quanlybds.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/paged")
    public ResponseEntity<Page<Payment>> getPaymentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(paymentService.getPaymentsPaged(page, size, keyword, status));
    }

    // Thêm API duyệt nhanh trạng thái
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return ResponseEntity.ok(paymentService.updateStatus(id, newStatus));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/revenueThisMonth")
    public ResponseEntity<Double> getTotalPaymentsThisMonth() {
        return ResponseEntity.ok(paymentService.getTotalPaymentsThisMonth());
    }

    @GetMapping("/revenueByMonth")
    public ResponseEntity<?> getRevenueByMonth() {
        return ResponseEntity.ok(paymentService.getRevenueByMonth());
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(payment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@Valid @RequestBody Payment payment, @PathVariable Integer id) {
        Payment updatedPayment = paymentService.updatePayment(id, payment);
        return ResponseEntity.ok(updatedPayment);
    }


    @GetMapping("/recent")
    public ResponseEntity<?> getRecentPayments() {
        return ResponseEntity.ok(paymentService.getRecentPayments());
    }
}
