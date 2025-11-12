package com.inventra.app.service;

import com.inventra.app.entity.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    ProductDto save(ProductDto productDTO);
    ProductDto findById(Long id);
    List<ProductDto> findAll();
    void delete(Long id);
    ProductDto update(ProductDto productDTO);
    List<ProductDto> findByName(String name);
    Page<ProductDto> findByName(String name, Pageable pageable);
}
