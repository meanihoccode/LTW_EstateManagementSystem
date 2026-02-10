package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@CrossOrigin(origins = "*")
public class OwnerController {
    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public ResponseEntity<List<Owner>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable Integer id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @PostMapping
    public ResponseEntity<Owner> createOwner(@Valid @RequestBody Owner owner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.createOwner(owner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Owner> updateOwner(@PathVariable Integer id, @Valid @RequestBody Owner ownerDetails) {
        return ResponseEntity.ok(ownerService.updateOwner(id, ownerDetails));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Integer id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}