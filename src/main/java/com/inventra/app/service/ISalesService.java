package com.inventra.app.service;

import com.inventra.app.entity.dto.SalesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISalesService {
    SalesDto findById(Long id);
    SalesDto createSale(SalesDto salesDTO);
    SalesDto save(SalesDto salesDTO);

    // Listar ventas paginadas, opcional búsqueda por fecha (string)
    Page<SalesDto> listSales(String date, Pageable pageable);

    // Obtener detalle completo de una venta (productos vendidos con cantidad)
    SalesDto getSaleDetail(Long id);
}
