package com.inventra.app.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
public class SalesDto {
    private Long id;
    @NotNull(message = "name is required")
    @Positive(message = "quantity must be greater than zero")
    private Double total;
    private String status;
    @NotBlank(message = "client is required")
    private ClientDto client;
    private List<ProductDto> productList;
    private Instant date = new java.util.Date().toInstant();
}
