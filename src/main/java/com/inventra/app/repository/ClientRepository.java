package com.inventra.app.repository;

import com.inventra.app.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository <Client, Long>{
    // Búsqueda paginada por nombre (coincidencia parcial, case-insensitive)
    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
