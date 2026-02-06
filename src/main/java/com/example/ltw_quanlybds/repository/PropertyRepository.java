package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findByStatus(String status);

    List<Property> findByOwnerId(Integer ownerId);

    List<Property> findByNameContainingIgnoreCase(String name);
    void deleteById(Integer id);
}