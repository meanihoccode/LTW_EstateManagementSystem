package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import com.example.ltw_quanlybds.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional // Rất quan trọng: Đảm bảo nếu lưu Hợp đồng lỗi thì BĐS cũng sẽ rollback lại trạng thái cũ
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenantRepository tenantRepository;

    public Page<Contract> getContractsPaged(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return contractRepository.searchContracts(keyword, status, pageable);
    }

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

    // ===============================================
    // 1. TỰ ĐỘNG KHI THÊM MỚI HỢP ĐỒNG
    // ===============================================
    public Contract createContract(Contract contract) {
        if (contract.getPropertyId() != null) {
            Property property = propertyRepository.findById(contract.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + contract.getPropertyId()));

            // LOGIC KIỂM TRA & ĐỔI TRẠNG THÁI BĐS
            if (!"Trống".equals(property.getStatus())) {
                throw new RuntimeException("Bất động sản này đang không Trống, không thể tạo hợp đồng!");
            }
            property.setStatus("Cho thuê");
            propertyRepository.save(property); // Lưu trạng thái mới của BĐS

            contract.setProperty(property);
        }

        if (contract.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(contract.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + contract.getTenantId()));
            contract.setTenant(tenant);
        }

        return contractRepository.save(contract);
    }

    // ===============================================
    // 2. TỰ ĐỘNG KHI CẬP NHẬT HỢP ĐỒNG
    // ===============================================
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

        // LOGIC KHI ĐỔI TRẠNG THÁI HỢP ĐỒNG
        if (contractDetails.getStatus() != null) {
            contract.setStatus(contractDetails.getStatus());

            // Nếu hợp đồng chuyển sang "Kết thúc" thì phải trả BĐS về trạng thái "Trống"
            if ("Kết thúc".equals(contractDetails.getStatus())) {
                Property property = contract.getProperty();
                if (property != null) {
                    property.setStatus("Trống");
                    propertyRepository.save(property);
                }
            }
        }

        if (contractDetails.getPropertyId() != null) {
            Property property = propertyRepository.findById(contractDetails.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + contractDetails.getPropertyId()));
            contract.setProperty(property);
        }
        if (contractDetails.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(contractDetails.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + contractDetails.getTenantId()));
            contract.setTenant(tenant);
        }

        return contractRepository.save(contract);
    }

    // ===============================================
    // 3. TỰ ĐỘNG KHI XÓA HỢP ĐỒNG
    // ===============================================
    public void deleteContract(Integer id) {
        Contract contract = getContractById(id);

        // Trả BĐS về trạng thái Trống trước khi xóa hợp đồng
        Property property = contract.getProperty();
        if (property != null) {
            property.setStatus("Trống");
            propertyRepository.save(property);
        }

        contractRepository.deleteById(id);
    }

    public long getTotalActiveContracts() {
        return contractRepository.countByStatus("Hiệu lực") + contractRepository.countByStatus("Chờ duyệt");
    }

    public List<Contract> getExpiringContracts() {
        return contractRepository.findExpiringContracts();
    }
}