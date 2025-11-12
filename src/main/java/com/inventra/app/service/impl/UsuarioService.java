package com.inventra.app.service.impl;

import com.inventra.app.config.exceptions.CustomServiceException;
import com.inventra.app.entity.User;
import com.inventra.app.entity.dto.RegisterDTO;
import com.inventra.app.entity.dto.RoleDTO;
import com.inventra.app.entity.dto.UserDTO;
import com.inventra.app.repository.UserRepository;
import com.inventra.app.service.IRoleService;
import com.inventra.app.service.IUserService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService implements IUserService {

    private static final Logger logger = LogManager.getLogger(UsuarioService.class);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;

    @Override
    public Optional<UserDTO> getByUsername(String username) {
        logger.info("** Getting user by username **");
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            logger.error("** User not found **");
            throw new CustomServiceException("123", "E003", "usuario o clave incorrecto :/");
        }

        UserDTO usuarioDTO = new UserDTO();
        usuarioDTO.setId(user.get().getId());
        usuarioDTO.setUsername(user.get().getUsername());
        usuarioDTO.setName(user.get().getName());
        usuarioDTO.setPassword(user.get().getPassword());
        usuarioDTO.setEmail(user.get().getEmail());

        logger.info("** User found **");
        return Optional.of(usuarioDTO);
    }

    @Transactional
    @Override
    public UserDTO registerUser(RegisterDTO registerDto, String uuid) {
        logger.info("** Registering user **");
        var userDb = getUserByEmail(registerDto.getEmail());

        if (userDb.isPresent()) {
            if (userRepository.existsByEmail(userDb.get().getEmail())) {
                logger.error("** User already exists **");
                throw new CustomServiceException(uuid, "E409", "User already exists with that email.");
            }
        }

        var username = userRepository.findByUsername(registerDto.getUsername());
        if (username.isPresent()) {
            logger.error("** User with that username already exists **");
            throw new CustomServiceException(uuid, "E409", "User with that username already exists.");
        }

        RoleDTO rol;
        if (registerDto.getRol() == null || registerDto.getRol().getId() == null) {
            logger.error("** Role ADMIN **");
            rol = roleService.findById(1L);
        } else {
            logger.info("** Role " + registerDto.getRol().getId() + " **");
            rol = roleService.findById(registerDto.getRol().getId());
        }

        UserDTO userDto = modelMapper.map(registerDto, UserDTO.class);
        userDto.setUsername(registerDto.getUsername());
        userDto.setRole(rol);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userDto.setUsername(userDto.getUsername().toLowerCase().trim());

        var mapper = modelMapper.map(userDto, User.class);
        var userSaved = userRepository.save(mapper);
        registerDto.setId(userSaved.getId());
        registerDto.setRol(rol);

        logger.info("** User registered **");
        return modelMapper.map(registerDto, UserDTO.class);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}