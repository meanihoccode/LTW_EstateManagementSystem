package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
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
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;


    public Page<Tenant> getTenantsPaged(int page, int size, String keyword) {
        // Sắp xếp khách thuê mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return tenantRepository.searchTenants(keyword, pageable);
    }
    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }
    public Tenant getById(Integer id) {
        return tenantRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Tenant not found with id: " + id));
    }
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
    public Tenant updateTenant(Tenant tenant) {
        Tenant existingTenant = getById(tenant.getId());

        if (tenant.getFullName() != null) {
            existingTenant.setFullName(tenant.getFullName());
        }
        if (tenant.getEmail() != null) {
            existingTenant.setEmail(tenant.getEmail());
        }
        if (tenant.getPhone() != null) {
            existingTenant.setPhone(tenant.getPhone());
        }
        if (tenant.getIdNumber() != null) {
            existingTenant.setIdNumber(tenant.getIdNumber());
        }
        return tenantRepository.save(existingTenant);
    }
    public void deleteTenant(Integer id) {
        tenantRepository.deleteById(id);
    }
}
