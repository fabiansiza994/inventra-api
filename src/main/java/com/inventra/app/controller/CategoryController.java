package com.inventra.app.controller;


import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.CategoryDto;
import com.inventra.app.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createCategory(@RequestBody @Valid CategoryDto request,
                                                              BindingResult result) {
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
        var product = categoryService.save(request);
        return ResponseHandler.successResponse(product, uuid.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listCategories() {
        UUID uuid = UUID.randomUUID();
        var categories = categoryService.findAll();
        return ResponseHandler.successResponse(categories, uuid.toString());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchCategories(@RequestParam(value = "name", required = false) String name,
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
        var categoriesPage = categoryService.findByName(name, pageable);
        return ResponseHandler.successResponse(categoriesPage, uuid.toString());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> getCategories(@PathVariable Long categoryId) {
        UUID uuid = UUID.randomUUID();
        var category = categoryService.findById(categoryId);
        return ResponseHandler.successResponse(category, uuid.toString());
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable Long categoryId) {
        UUID uuid = UUID.randomUUID();
        categoryService.delete(categoryId);
        return ResponseHandler.successResponse(null, uuid.toString());
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> updateCategory(@RequestBody @Valid CategoryDto request, BindingResult result) {
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
        var product = categoryService.update(request);
        return ResponseHandler.successResponse(product, uuid.toString());
    }
}
