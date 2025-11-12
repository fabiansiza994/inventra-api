package com.inventra.app.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CategoryDto {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String status;
    private String icon;
}
