package com.inventra.app.repository;

import com.inventra.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Buscar por coincidencia parcial en el nombre, case-insensitive
    List<Product> findByNameContainingIgnoreCase(String name);

    // Versión paginada
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Verificar si existen productos asociados a una categoría
    boolean existsByCategory_Id(Long categoryId);
}
