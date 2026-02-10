package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;

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
        return contractRepository.save(contract);
    }

    public Contract updateContract(Integer id, Contract contractDetails) {
        Contract contract = getContractById(id);

        if (contractDetails.getProperty() != null) {
            contract.setProperty(contractDetails.getProperty());
        }
        if (contractDetails.getTenant() != null) {
            contract.setTenant(contractDetails.getTenant());
        }
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

        return contractRepository.save(contract);
    }

    public void deleteContract(Integer id) {
        contractRepository.deleteById(id);
    }
}