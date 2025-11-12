package com.inventra.app.service;

import com.inventra.app.entity.dto.RoleDTO;

public interface IRoleService {
    RoleDTO save(RoleDTO rolDTO);

    RoleDTO findById(Long id);
}
