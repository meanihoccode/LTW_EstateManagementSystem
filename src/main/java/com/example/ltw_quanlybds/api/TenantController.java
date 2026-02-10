package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Tenant;
import com.example.ltw_quanlybds.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {
    @Autowired
    private TenantService tenantService;
    @GetMapping
    public ResponseEntity<List<Tenant>> getAll() {
        return ResponseEntity.ok(tenantService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(tenantService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant) {
        Tenant createdTenant = tenantService.createTenant(tenant);
        return ResponseEntity.status(201).body(createdTenant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Integer id, @Valid @RequestBody Tenant tenant) {
        tenant.setId(id);
        Tenant updatedTenant = tenantService.updateTenant(tenant);
        return ResponseEntity.ok(updatedTenant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Integer id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}

