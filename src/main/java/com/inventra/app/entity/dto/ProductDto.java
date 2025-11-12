package com.inventra.app.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Long id;
    @NotBlank(message = "name is required")
    private String name;
    private String description;
    @NotNull(message = "price is required")
    @Positive(message = "price must be greater than zero")
    private Double price;
    private String image;
    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than zero")
    private Integer quantity;
    @Valid
    private CategoryDto category;
    private String brand;
    @NotBlank(message = "status is required")
    private String status;
}
