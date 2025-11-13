package com.inventra.app.service;

import com.inventra.app.entity.dto.RegisterDTO;
import com.inventra.app.entity.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IUserService {
    Optional<UserDTO> getByUsername(String username);

    UserDTO registerUser(RegisterDTO registroDTO, String uuid);

    // Listado paginado (con filtro opcional por nombre/username)
    Page<UserDTO> findByName(String name, Pageable pageable);

    // Detalle por id
    UserDTO findById(Long id);

    // Actualización de usuario
    UserDTO update(UserDTO userDTO, String uuid);
}
