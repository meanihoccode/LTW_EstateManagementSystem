

package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.PaymentRepository;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import com.example.ltw_quanlybds.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Contract> getAllContract() {
        return contractRepository.findAll();
    }

    public List<Contract> getContractsByStatus(String status) {
        return contractRepository.findByStatus(status);
    }

    public Contract getContractById(Integer id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    public Contract createContract(Contract contract) {
        if (contract.getPropertyId() != null) {
            Property property = propertyRepository.findById(contract.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contract.getPropertyId()));
            contract.setProperty(property);
        }

        if (contract.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(contract.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contract.getTenantId()));
            contract.setTenant(tenant);
        }
        return contractRepository.save(contract);
    }

    public Contract updateContract(Integer id, Contract contractDetails) {
        Contract contract = getContractById(id);

        if (contractDetails.getStartDate() != null) {
            contract.setStartDate(contractDetails.getStartDate());
        }
        if (contractDetails.getEndDate() != null) {
            contract.setEndDate(contractDetails.getEndDate());
        }
        if (contractDetails.getDeposit() != null) {
            contract.setDeposit(contractDetails.getDeposit());
        }
        if (contractDetails.getStatus() != null) {
            contract.setStatus(contractDetails.getStatus());
        }

        if (contractDetails.getPropertyId() != null) {
            Property property = propertyRepository.findById(contractDetails.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractDetails.getPropertyId()));
            contract.setProperty(property);
        }
        if (contractDetails.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(contractDetails.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractDetails.getTenantId()));
            contract.setTenant(tenant);
        }

        return contractRepository.save(contract);
    }

    public void deleteContract(Integer id) {
        contractRepository.deleteById(id);
    }
    public long getTotalActiveContracts() {
        return contractRepository.countByStatus("Hiệu lực") + contractRepository.countByStatus("Chờ duyệt");
    }

    public Double getRevenueThisMonth() {
        return paymentRepository.calculateTotalPaymentsThisMonth();
    }

    public List<Object[]> getRevenueByMonth() {
        return paymentRepository.getRevenueByMonth();
    }
}