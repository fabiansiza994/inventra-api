package com.inventra.app.service;

import com.inventra.app.entity.dto.RegisterDTO;
import com.inventra.app.entity.dto.UserDTO;

import java.util.Optional;

public interface IUserService {
    Optional<UserDTO> getByUsername(String username);

    UserDTO registerUser(RegisterDTO registroDTO, String uuid);
}
