package com.inventra.app.service;

import com.inventra.app.entity.dto.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    CategoryDto save(CategoryDto categoryDTO);
    Optional<CategoryDto> findById(Long id);
    List<CategoryDto> findAll();
    void delete(Long id);
    CategoryDto update(CategoryDto categoryDTO);
    List<CategoryDto> findByName(String name);
    Page<CategoryDto> findByName(String name, Pageable pageable);
}
