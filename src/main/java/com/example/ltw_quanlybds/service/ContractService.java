package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Account;
import com.example.ltw_quanlybds.entity.Contract;
import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.AccountRepository;
import com.example.ltw_quanlybds.repository.ContractRepository;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import com.example.ltw_quanlybds.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private AccountRepository accountRepository;

    // Lấy Role của người đang thao tác
    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            Account acc = accountRepository.findByUsername(auth.getName());
            if (acc != null) return acc.getRole();
        }
        return "Nhân viên"; // Trả về mặc định an toàn nhất
    }

    public Page<Contract> getContractsPaged(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return contractRepository.searchContracts(keyword, status, pageable);
    }

    // ===============================================
    // HÀM DÀNH RIÊNG CHO CHỨC NĂNG DUYỆT NHANH
    // ===============================================
    public Contract updateStatus(Integer id, String newStatus) {
        Contract contract = getContractById(id);

        // 1. Cập nhật trạng thái hợp đồng
        contract.setStatus(newStatus);

        // 2. Chạy lại logic đổi trạng thái BĐS thông minh
        Property property = contract.getProperty();
        if (property != null) {
            LocalDate today = LocalDate.now();

            if ("Kết thúc".equals(newStatus) || "Từ chối".equals(newStatus) || "Đã hủy".equals(newStatus)) {
                property.setStatus("Trống");
            }
            else if ("Chờ duyệt".equals(newStatus)) {
                property.setStatus("Đang giữ chỗ");
            }
            else if ("Hiệu lực".equals(newStatus)) {
                if (contract.getStartDate().isAfter(today)) {
                    property.setStatus("Đã đặt cọc");
                } else {
                    property.setStatus("Cho thuê");
                }
            }
            propertyRepository.save(property);
        }

        return contractRepository.save(contract);
    }

    public List<Contract> getAllContract() {
        return contractRepository.findAll();
    }

    public Contract getContractById(Integer id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
    }

    // ===============================================
    // 1. TẠO MỚI HỢP ĐỒNG
    // ===============================================
    public Contract createContract(Contract contract) {
        // BƯỚC 1: KIỂM TRA TRÙNG LẶP THỜI GIAN
        long overlaps = contractRepository.countOverlappingContracts(
                contract.getPropertyId(),
                contract.getStartDate(),
                contract.getEndDate(),
                -1
        );
        if (overlaps > 0) {
            throw new RuntimeException("Thời gian này Bất động sản đã có người thuê hoặc đặt cọc!");
        }

        // BƯỚC 2: ÉP QUYỀN (Maker - Checker)
        if ("Nhân viên".equals(getCurrentUserRole())) {
            contract.setStatus("Chờ duyệt");
        }

        // BƯỚC 3: CẬP NHẬT TRẠNG THÁI BĐS THÔNG MINH
        if (contract.getPropertyId() != null) {
            Property property = propertyRepository.findById(contract.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Bất động sản"));

            LocalDate today = LocalDate.now();

            if ("Chờ duyệt".equals(contract.getStatus())) {
                property.setStatus("Đang giữ chỗ"); // Đang chờ sếp duyệt
            } else if ("Hiệu lực".equals(contract.getStatus())) {
                if (contract.getStartDate().isAfter(today)) {
                    property.setStatus("Đã đặt cọc"); // Khách thuê ở tương lai
                } else {
                    property.setStatus("Cho thuê"); // Khách thuê luôn hôm nay
                }
            }

            propertyRepository.save(property);
            contract.setProperty(property);
        }

        if (contract.getTenantId() != null) {
            Tenant tenant = tenantRepository.findById(contract.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Khách thuê"));
            contract.setTenant(tenant);
        }

        return contractRepository.save(contract);
    }

    // ===============================================
    // 2. CẬP NHẬT & DUYỆT HỢP ĐỒNG
    // ===============================================
    public Contract updateContract(Integer id, Contract contractDetails) {
        Contract contract = getContractById(id);

        // BƯỚC 1: KIỂM TRA TRÙNG LẶP THỜI GIAN
        Integer propId = contractDetails.getPropertyId() != null ? contractDetails.getPropertyId() : contract.getProperty().getId();
        LocalDate sDate = contractDetails.getStartDate() != null ? contractDetails.getStartDate() : contract.getStartDate();
        LocalDate eDate = contractDetails.getEndDate() != null ? contractDetails.getEndDate() : contract.getEndDate();

        long overlaps = contractRepository.countOverlappingContracts(propId, sDate, eDate, id);
        if (overlaps > 0) {
            throw new RuntimeException("Thời gian này Bất động sản đã có người thuê hoặc đặt cọc!");
        }

        // BƯỚC 2: CẬP NHẬT THÔNG TIN
        if (contractDetails.getStartDate() != null) contract.setStartDate(contractDetails.getStartDate());
        if (contractDetails.getEndDate() != null) contract.setEndDate(contractDetails.getEndDate());
        if (contractDetails.getDeposit() != null) contract.setDeposit(contractDetails.getDeposit());

        // BƯỚC 3: XỬ LÝ TRẠNG THÁI & ROLE
        if ("Nhân viên".equals(getCurrentUserRole())) {
            contract.setStatus("Chờ duyệt"); // Nhân viên sửa thì quay về chờ duyệt
        } else if (contractDetails.getStatus() != null) {
            contract.setStatus(contractDetails.getStatus()); // Quản lý/Admin duyệt
        }

        // BƯỚC 4: TỰ ĐỘNG ĐỔI TRẠNG THÁI BĐS
        Property property = contract.getProperty();
        if (property != null) {
            String cStatus = contract.getStatus();
            LocalDate today = LocalDate.now();

            if ("Kết thúc".equals(cStatus) || "Từ chối".equals(cStatus) || "Đã hủy".equals(cStatus)) {
                property.setStatus("Trống");
            }
            else if ("Chờ duyệt".equals(cStatus)) {
                property.setStatus("Đang giữ chỗ");
            }
            else if ("Hiệu lực".equals(cStatus)) {
                if (contract.getStartDate().isAfter(today)) {
                    property.setStatus("Đã đặt cọc");
                } else {
                    property.setStatus("Cho thuê");
                }
            }
            propertyRepository.save(property);
        }

        if (contractDetails.getPropertyId() != null) {
            Property newProp = propertyRepository.findById(contractDetails.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy BĐS"));
            contract.setProperty(newProp);
        }
        if (contractDetails.getTenantId() != null) {
            Tenant newTenant = tenantRepository.findById(contractDetails.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Khách thuê"));
            contract.setTenant(newTenant);
        }

        return contractRepository.save(contract);
    }

    public long getTotalActiveContracts() {
        return contractRepository.countByStatus("Hiệu lực") + contractRepository.countByStatus("Chờ duyệt");
    }

    public List<Contract> getExpiringContracts() {
        return contractRepository.findExpiringContracts();
    }
}