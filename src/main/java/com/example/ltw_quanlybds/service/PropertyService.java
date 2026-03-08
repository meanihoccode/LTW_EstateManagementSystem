package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Property;
import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.entity.User;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.PropertyRepository;
import com.example.ltw_quanlybds.repository.OwnerRepository;
import com.example.ltw_quanlybds.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Property> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties;
    }

    public Property getPropertyById(Integer id) {
        return propertyRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    public List<Property> getPropertiesByStatus(String status) {
        return propertyRepository.findByStatus(status);
    }

    public Property createProperty(Property property) {
        // Nếu frontend gửi ownerId, tìm owner từ database
        if (property.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(property.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + property.getOwnerId()));
            property.setOwner(owner);
        }

        // Nếu frontend gửi staffId, tìm staff từ database
        if (property.getStaffId() != null) {
            User staff = userRepository.findById(property.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + property.getStaffId()));
            property.setStaff(staff);
        }

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

        // Cập nhật owner nếu có ownerId
        if (propertyDetails.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(propertyDetails.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + propertyDetails.getOwnerId()));
            property.setOwner(owner);
        }

        // Cập nhật staff nếu có staffId
        if (propertyDetails.getStaffId() != null) {
            User staff = userRepository.findById(propertyDetails.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + propertyDetails.getStaffId()));
            property.setStaff(staff);
        }

        return propertyRepository.save(property);
    }
    public void deletePropertyById(Integer id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found");
        }
        propertyRepository.deleteById(id);
    }

    public long getTotalProperties() {
        return propertyRepository.count();
    }

    public long getEmptyProperties() {
        return propertyRepository.countByStatus("Trống");
    }
}