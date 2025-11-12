package com.inventra.app.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {

    private Long id;
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @NotNull(message = "role is required")
    @Valid
    private RoleDTO rol = new RoleDTO();
}