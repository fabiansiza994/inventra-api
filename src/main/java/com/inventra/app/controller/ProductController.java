package com.inventra.app.controller;

import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.ProductDto;
import com.inventra.app.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createProduct(@RequestBody @Valid ProductDto request, BindingResult result) {
        UUID uuid = UUID.randomUUID();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }
        var product = productService.save(request);
        return ResponseHandler.successResponse(product, uuid.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listProducts() {
        UUID uuid = UUID.randomUUID();
        var products = productService.findAll();
        return ResponseHandler.successResponse(products, uuid.toString());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchProducts(@RequestParam(value = "name", required = false) String name,
                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "10") int size) {
        UUID uuid = UUID.randomUUID();
        // Validación de page/size
        List<ErrorItemDTO> errores = new ArrayList<>();
        final int MAX_SIZE = 100;
        if (page < 0) {
            errores.add(new ErrorItemDTO("E400", "Page must be >= 0", "page"));
        }
        if (size <= 0) {
            errores.add(new ErrorItemDTO("E400", "Size must be > 0", "size"));
        } else if (size > MAX_SIZE) {
            errores.add(new ErrorItemDTO("E400", "Size must be <= " + MAX_SIZE, "size"));
        }
        if (!errores.isEmpty()) {
            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }

        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var productsPage = productService.findByName(name, pageable);
        return ResponseHandler.successResponse(productsPage, uuid.toString());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Object>> getProducts(@PathVariable Long productId) {
        UUID uuid = UUID.randomUUID();
        var products = productService.findById(productId);
        return ResponseHandler.successResponse(products, uuid.toString());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable Long productId) {
        UUID uuid = UUID.randomUUID();
        productService.delete(productId);
        return ResponseHandler.successResponse(null, uuid.toString());
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> updateProduct(@RequestBody @Valid ProductDto request, BindingResult result) {
        UUID uuid = UUID.randomUUID();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }
        var product = productService.update(request);
        return ResponseHandler.successResponse(product, uuid.toString());
    }

}