package com.inventra.app.service.impl;

import com.inventra.app.entity.Role;
import com.inventra.app.entity.dto.RoleDTO;
import com.inventra.app.repository.RoleRepository;
import com.inventra.app.service.IRoleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class RolService implements IRoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RolService(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public RoleDTO save(RoleDTO rolDTO) {
        var rol = roleRepository.save(
                modelMapper.map(rolDTO, Role.class)
        );
        return modelMapper.map(rol, RoleDTO.class);
    }

    @Override
    public RoleDTO findById(Long id) {
        var rol = roleRepository.findById(id).orElse(null);
        return modelMapper.map(rol, RoleDTO.class);
    }
}
