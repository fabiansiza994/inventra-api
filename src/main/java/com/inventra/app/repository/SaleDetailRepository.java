package com.inventra.app.repository;

import com.inventra.app.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {
    List<SaleDetail> findBySaleId_Id(Long saleId);

    // Verificar si existen detalles de venta asociados a un producto
    boolean existsByProductId_Id(Long productId);
}
