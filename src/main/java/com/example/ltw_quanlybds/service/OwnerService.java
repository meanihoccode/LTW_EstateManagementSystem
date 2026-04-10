package com.example.ltw_quanlybds.service;

import com.example.ltw_quanlybds.entity.Owner;
import com.example.ltw_quanlybds.exception.ResourceNotFoundException;
import com.example.ltw_quanlybds.repository.OwnerRepository;
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

public class OwnerService {
    @Autowired
    private OwnerRepository ownerRepository;

    public Page<Owner> getOwnersPaged(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ownerRepository.searchOwners(keyword, pageable);
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Owner getOwnerById(Integer id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
    }

    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner updateOwner(Integer id, Owner ownerDetails) {
        Owner owner = getOwnerById(id);

        if (ownerDetails.getFullName() != null) {
            owner.setFullName(ownerDetails.getFullName());
        }
        if (ownerDetails.getPhone() != null) {
            owner.setPhone(ownerDetails.getPhone());
        }
        if (ownerDetails.getEmail() != null) {
            owner.setEmail(ownerDetails.getEmail());
        }
        if (ownerDetails.getAddress() != null) {
            owner.setAddress(ownerDetails.getAddress());
        }

        return ownerRepository.save(owner);
    }

    public void deleteOwner(Integer id) {
        ownerRepository.deleteById(id);
    }
}