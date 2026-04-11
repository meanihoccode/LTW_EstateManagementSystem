package com.example.ltw_quanlybds.repository;

import com.example.ltw_quanlybds.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findByStatus(String status);

    List<Property> findByOwnerId(Integer ownerId);
    List<Property> findByNameContainingIgnoreCase(String name);
    void deleteById(Integer id);

    long countByStatus(String status);

    // Tìm kiếm theo tên/địa chỉ và lọc theo trạng thái (có phân trang)
    @Query("SELECT p FROM Property p WHERE " +
            "(:status IS NULL OR :status = '' OR p.status = :status) AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.address LIKE %:keyword%)")
    Page<Property> searchProperties(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

    // Ép Spring Boot phải tìm theo p.owner.id (object owner thực sự)
    @Query("SELECT COUNT(p) > 0 FROM Property p WHERE p.owner.id = :ownerId")
    boolean existsByOwnerId(@Param("ownerId") Integer ownerId);
}