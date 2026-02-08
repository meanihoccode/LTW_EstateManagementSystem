package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }
    public Tenant getById(Integer id) {
        return tenantRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Tenant not found"));
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
