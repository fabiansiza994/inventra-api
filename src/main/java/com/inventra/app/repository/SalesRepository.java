package com.inventra.app.repository;

import com.inventra.app.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository <Sale, Long>{
    // Buscar por fecha (contiene), como la fecha se almacena como String
    Page<Sale> findByDateContaining(String date, Pageable pageable);
}
