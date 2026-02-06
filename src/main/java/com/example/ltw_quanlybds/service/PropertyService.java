package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional

public class PropertyService {
    // Business logic related to properties can be added here
    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        System.out.println("DEBUG: getAllProperties() returned " + properties.size() + " records");
        return properties;
    }

    public Property getPropertyById(Integer id) {
        return propertyRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Property not found"));
    }

    public List<Property> getPropertiesByStatus(String status) {
        return propertyRepository.findByStatus(status);
    }
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public Property updateProperty(Integer id, Property propertyDetails) {
        Property property = getPropertyById(id);

        if (propertyDetails.getName() != null) {
            property.setName(propertyDetails.getName());
        }
        if (propertyDetails.getType() != null) {
            property.setType(propertyDetails.getType());
        }
        if (propertyDetails.getAddress() != null) {
            property.setAddress(propertyDetails.getAddress());
        }
        if (propertyDetails.getArea() != null) {
            property.setArea(propertyDetails.getArea());
        }
        if (propertyDetails.getRentalPrice() != null) {
            property.setRentalPrice(propertyDetails.getRentalPrice());
        }
        if (propertyDetails.getStatus() != null) {
            property.setStatus(propertyDetails.getStatus());
        }
        if (propertyDetails.getOwnerId() != null) {
            property.setOwnerId(propertyDetails.getOwnerId());
        }

        return propertyRepository.save(property);
    }
    public void deletePropertyById(Integer id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found");
        }
        propertyRepository.deleteById(id);
    }
}