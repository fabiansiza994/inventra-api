package com.inventra.app.repository;

import com.inventra.app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String code);

    // Buscar por coincidencia parcial en el nombre, case-insensitive
    List<Category> findByNameContainingIgnoreCase(String name);

    // Versión paginada
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
