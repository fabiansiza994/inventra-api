package com.inventra.app.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    //private List<User> users;
}
