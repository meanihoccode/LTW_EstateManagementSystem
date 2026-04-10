package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Contract;
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
@Transactional
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
        // BƯỚC 1: KIỂM TRA TRÙNG LẶP THỜI GIAN TRƯỚC TIÊN
        long overlaps = contractRepository.countOverlappingContracts(
                contract.getPropertyId(),
                contract.getStartDate(),
                contract.getEndDate(),
                -1 // Truyền -1 vì là tạo mới, chưa có ID hợp đồng
        );

        if (overlaps > 0) {
            throw new RuntimeException("Thời gian này Bất động sản đã có người thuê hoặc đặt cọc!");
        }

        // BƯỚC 2: CẬP NHẬT THÔNG TIN VÀ TRẠNG THÁI
        if (contract.getPropertyId() != null) {
            Property property = propertyRepository.findById(contract.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + contract.getPropertyId()));

            // Nếu bạn cho phép "Đặt cọc trước" cho tháng sau, bạn có thể XÓA đoạn if check "Trống" này đi.
            // Nếu giữ lại, nhà phải đang trống thì mới tạo được hợp đồng.
//            if (!"Trống".equals(property.getStatus())) {
//                throw new RuntimeException("Bất động sản này đang không Trống, không thể tạo hợp đồng!");
//            }

            property.setStatus("Cho thuê");
            propertyRepository.save(property);

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

        // BƯỚC 1: KIỂM TRA TRÙNG LẶP THỜI GIAN
        Integer propId = contractDetails.getPropertyId() != null ? contractDetails.getPropertyId() : contract.getProperty().getId();
        java.time.LocalDate sDate = contractDetails.getStartDate() != null ? contractDetails.getStartDate() : contract.getStartDate();
        java.time.LocalDate eDate = contractDetails.getEndDate() != null ? contractDetails.getEndDate() : contract.getEndDate();

        long overlaps = contractRepository.countOverlappingContracts(propId, sDate, eDate, id);

        if (overlaps > 0) {
            throw new RuntimeException("Thời gian này Bất động sản đã có người thuê hoặc đặt cọc!");
        }

        // BƯỚC 2: CẬP NHẬT CÁC TRƯỜNG DỮ LIỆU
        if (contractDetails.getStartDate() != null) contract.setStartDate(contractDetails.getStartDate());
        if (contractDetails.getEndDate() != null) contract.setEndDate(contractDetails.getEndDate());
        if (contractDetails.getDeposit() != null) contract.setDeposit(contractDetails.getDeposit());

        // LOGIC ĐỔI TRẠNG THÁI
        if (contractDetails.getStatus() != null) {
            contract.setStatus(contractDetails.getStatus());

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

        // Trả BĐS về trạng thái Trống
        Property property = contract.getProperty();
        if (property != null) {
            property.setStatus("Trống");
            propertyRepository.save(property);
        }

        // Xóa thẳng tay, không check overlaps ở đây
        contractRepository.deleteById(id);
    }

    public long getTotalActiveContracts() {
        return contractRepository.countByStatus("Hiệu lực") + contractRepository.countByStatus("Chờ duyệt");
    }

    public List<Contract> getExpiringContracts() {
        return contractRepository.findExpiringContracts();
    }
}