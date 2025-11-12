package com.inventra.app.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ClientDto {
    private Long id;
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "email is required")
    private String email;
    private String phone;
    private String address;
    private String status = "ACTIVE";
}
