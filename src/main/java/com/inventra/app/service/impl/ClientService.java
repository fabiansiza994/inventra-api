package com.inventra.app.service.impl;

import com.inventra.app.config.exceptions.CustomServiceException;
import com.inventra.app.entity.Client;
import com.inventra.app.entity.dto.ClientDto;
import com.inventra.app.repository.ClientRepository;
import com.inventra.app.service.IClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService implements IClientService {

    private static final Logger logger = LogManager.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    public ClientService(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ClientDto save(ClientDto clientDTO) {
        var client = modelMapper.map(clientDTO, Client.class);
        return modelMapper.map(clientRepository.save(client), ClientDto.class);
    }

    @Override
    public ClientDto findById(Long id) {
        logger.info("** Finding client by id **");
        var client = clientRepository.findById(id);
        if (client.isEmpty()) {
            logger.error("** Client not found **");
            throw new CustomServiceException("123", "E001", "Client not found");
        }
        logger.info("** Client found **");
        return modelMapper.map(client.get(), ClientDto.class);
    }

    @Override
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public ClientDto update(ClientDto clientDTO) {
        validateClient(clientDTO.getId());
        var client = clientRepository.save(modelMapper.map(clientDTO, Client.class));
        return modelMapper.map(client, ClientDto.class);
    }

    @Override
    public List<ClientDto> findAll() {
        var clients = clientRepository.findAll();
         return clients.stream()
                .map(client -> modelMapper.map(client, ClientDto.class))
                .toList();
    }

    @Override
    public Page<ClientDto> findByName(String name, Pageable pageable) {
        Page<Client> page;
        if (name == null || name.trim().isEmpty()) {
            page = clientRepository.findAll(pageable);
        } else {
            page = clientRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        List<ClientDto> dtos = page.getContent().stream()
                .map(client -> modelMapper.map(client, ClientDto.class))
                .toList();
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public void validateClient(Long clientId) {
        var client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new CustomServiceException("123", "E001", "Client not found");
        }
    }
}
