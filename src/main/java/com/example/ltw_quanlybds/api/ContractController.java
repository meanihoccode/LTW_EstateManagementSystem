package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;


    @GetMapping("/paged")
    public ResponseEntity<Page<Contract>> getAllContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(contractService.getContractsPaged(page, size, keyword, status));
    }

    @GetMapping
    public ResponseEntity<List<Contract>> findAll() {
        return ResponseEntity.ok(contractService.getAllContract());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Contract> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@Valid @RequestBody Contract contract) {
        Contract createdContract = contractService.createContract(contract);
        return ResponseEntity.status(201).body(createdContract);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Integer id, @Valid @RequestBody Contract contract) {
        Contract updatedContract = contractService.updateContract(id, contract);
        return ResponseEntity.ok(updatedContract);
    }

    @GetMapping("/totalActiveContracts")
    public ResponseEntity<Long> getTotalActiveContracts() {
        long totalActiveContracts = contractService.getTotalActiveContracts();
        return ResponseEntity.ok(totalActiveContracts);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Contract> updateContractStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return ResponseEntity.ok(contractService.updateStatus(id, newStatus));
    }
    @GetMapping("/expiringContracts")
    public ResponseEntity<?> getExpiringContracts() {
        return ResponseEntity.ok(contractService.getExpiringContracts());
    }
}
