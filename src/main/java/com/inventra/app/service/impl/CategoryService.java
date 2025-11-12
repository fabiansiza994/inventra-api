package com.inventra.app.service.impl;

import com.inventra.app.config.exceptions.CustomServiceException;
import com.inventra.app.entity.Category;
import com.inventra.app.entity.dto.CategoryDto;
import com.inventra.app.repository.CategoryRepository;
import com.inventra.app.repository.ProductRepository;
import com.inventra.app.service.ICategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public CategoryDto save(CategoryDto categoryDTO) {
        logger.info("** Saving category **");
        var categoryDb = categoryRepository.findByCode(categoryDTO.getCode());
        if (categoryDb.isPresent()) {
            throw new CustomServiceException("123", "E001", "Category with code " +
                    categoryDTO.getCode() + " already exists");
        }
        var category = categoryRepository.save(modelMapper.map(categoryDTO, Category.class));
        logger.info("** Category saved **");
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public Optional<CategoryDto> findById(Long id) {
        logger.info("** Finding category by id **");
        var category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return Optional.empty();
        }
        logger.info("** Category found **");
        return category.map(c -> modelMapper.map(c, CategoryDto.class));
    }

    @Override
    public List<CategoryDto> findAll() {
        logger.info("** Finding all categories **");
        var categories = categoryRepository.findAll();
        logger.info("** Categories found: " + categories.size() + "**");
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();
    }

    @Override
    public void delete(Long id) {
        // TODO validar si la categoria tiene productos asociados
        logger.info("** Deleting category **");
        var category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            logger.error("** Category not found **");
            throw new CustomServiceException("123", "E001", "Category not found");
        }

        // Verificar si existen productos asociados a esta categoría
        boolean hasProducts = productRepository.existsByCategory_Id(id);
        if (hasProducts) {
            logger.error("** Category has products associated and cannot be deleted: " + id + " **");
            throw new CustomServiceException("123", "E009", "Category has products associated and cannot be deleted");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDTO) {
        logger.info("** Updating category **");
        var categoryDb = categoryRepository.findById(categoryDTO.getId());
        if (categoryDb.isEmpty()) {
            logger.error("** Category not found **");
            throw new CustomServiceException("123", "E001", "Category not found");
        }
        var category = categoryRepository.save(modelMapper.map(categoryDTO, Category.class));
        logger.info("** Category updated **");
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findByName(String name) {
        logger.info("** Finding categories by name: {} **", name);
        var categories = categoryRepository.findByNameContainingIgnoreCase(name);
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();
    }

    @Override
    public Page<CategoryDto> findByName(String name, Pageable pageable) {
        logger.info("** Finding categories by name paginated: {} **", name);
        List<Category> categories;
        if (name == null || name.trim().isEmpty()) {
            // Usar el método paginado del repositorio para eficiencia cuando no hay filtro
            var page = categoryRepository.findAll(pageable);
            List<CategoryDto> dtos = page.getContent().stream()
                    .map(category -> modelMapper.map(category, CategoryDto.class))
                    .toList();
            return new PageImpl<>(dtos, pageable, page.getTotalElements());
        } else {
            // obtener la lista completa por filtro y paginar en memoria
            categories = categoryRepository.findByNameContainingIgnoreCase(name);
            int total = categories.size();
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int fromIndex = pageNumber * pageSize;
            if (fromIndex >= total) {
                return new PageImpl<>(List.of(), pageable, total);
            }
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<CategoryDto> dtos = categories.subList(fromIndex, toIndex).stream()
                    .map(category -> modelMapper.map(category, CategoryDto.class))
                    .toList();
            return new PageImpl<>(dtos, pageable, total);
        }
    }

}
