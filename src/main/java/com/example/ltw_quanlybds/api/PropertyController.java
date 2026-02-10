package com.example.ltw_quanlybds.api;

import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getProperty(@PathVariable Integer id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Property>> getPropertiesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(propertyService.getPropertiesByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Property> createProperty(@Valid @RequestBody Property property) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.createProperty(property));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Integer id,
                                                   @Valid @RequestBody Property propertyDetails) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Integer id) {
        propertyService.deletePropertyById(id);
        return ResponseEntity.noContent().build();
    }
}