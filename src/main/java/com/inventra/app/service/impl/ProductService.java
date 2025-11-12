package com.inventra.app.service.impl;

import com.inventra.app.config.exceptions.CustomServiceException;
import com.inventra.app.entity.Product;
import com.inventra.app.entity.dto.ProductDto;
import com.inventra.app.repository.ProductRepository;
import com.inventra.app.repository.SaleDetailRepository;
import com.inventra.app.service.ICategoryService;
import com.inventra.app.service.IProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ICategoryService categoryService;
    private final ModelMapper modelMapper;
    private final SaleDetailRepository saleDetailRepository;

    public ProductService(ProductRepository productRepository, ICategoryService categoryService, ModelMapper modelMapper,
                          SaleDetailRepository saleDetailRepository) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.saleDetailRepository = saleDetailRepository;
    }

    @Override
    public ProductDto save(ProductDto productDTO) {
        logger.info("** Saving product **");
        validateCategory(productDTO.getCategory().getId());
        var product = productRepository.save(modelMapper.map(productDTO, Product.class));
        logger.info("** Product saved **");
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto findById(Long id) {
        logger.info("** Finding product by id **");
        if (id == null) {
            logger.error("** Product not found **");
            throw new CustomServiceException("123", "E001", "Product not found");
        }
        var product = productRepository.findById(id);
        if( product.isEmpty()){
            logger.error("** Product not found **");
            throw new CustomServiceException("123", "E001", "Product not found");
        }
        logger.info("** Product found **");
        return modelMapper.map(product.get(), ProductDto.class);
    }

    @Override
    public List<ProductDto> findAll() {
        logger.info("** Finding all products **");
        var products = productRepository.findAll();
        logger.info("** Products found: " + products.size() + "**");
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public void delete(Long id) {
        logger.info("** Deleting product **");
        var product = productRepository.findById(id);
        if (product.isEmpty()) {
            logger.error("** Product not found **");
            throw new CustomServiceException("123", "E001", "Product not found");
        }

        // Verificar si existen detalles de venta asociados a este producto
        boolean hasSales = saleDetailRepository.existsByProductId_Id(id);
        if (hasSales) {
            logger.error("** Product has sales associated and cannot be deleted: " + id + " **");
            throw new CustomServiceException("123", "E010", "Product has sales associated and cannot be deleted");
        }

        productRepository.deleteById(id);
    }

    @Override
    public ProductDto update(ProductDto productDTO) {
        logger.info("** Updating product **");
        var productDb = productRepository.findById(productDTO.getId());
        if (productDb.isEmpty()) {
            logger.error("** Product not found **");
            throw new CustomServiceException("123", "E001", "Product not found");
        }
        validateCategory(productDTO.getCategory().getId());

        var product = productRepository.save(modelMapper.map(productDTO, Product.class));
        logger.info("** Product updated **");
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findByName(String name) {
        logger.info("** Finding products by name: {} **", name);
        var products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public Page<ProductDto> findByName(String name, Pageable pageable) {
        logger.info("** Finding products by name paginated: {} **", name);
        Page<Product> page;
        if (name == null || name.trim().isEmpty()) {
            page = productRepository.findAll(pageable);
        } else {
            page = productRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        List<ProductDto> dtos = page.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private void validateCategory(Long categoryId) {
        logger.info("** Validating category **");
        var category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            logger.error("** Category not found **");
            throw new CustomServiceException("123", "E001", "Category not found");
        }
    }
}
