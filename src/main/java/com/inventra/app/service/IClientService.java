package com.inventra.app.service;

import com.inventra.app.entity.dto.ClientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClientService {
    ClientDto save(ClientDto clientDTO);
    ClientDto findById(Long id);
    void delete(Long id);
    ClientDto update(ClientDto clientDTO);
    List<ClientDto> findAll();

    // Búsqueda paginada por nombre
    Page<ClientDto> findByName(String name, Pageable pageable);
}
