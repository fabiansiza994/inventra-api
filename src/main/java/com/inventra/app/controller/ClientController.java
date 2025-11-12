package com.inventra.app.controller;

import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.ClientDto;
import com.inventra.app.service.IClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final IClientService clientService;

    public ClientController(IClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createClient(@RequestBody @Valid ClientDto request, BindingResult result) {
        UUID uuid = UUID.randomUUID();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }
        var product = clientService.save(request);
        return ResponseHandler.successResponse(product, uuid.toString());
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ApiResponse<Object>> deleteClient(@PathVariable Long clientId) {
        UUID uuid = UUID.randomUUID();
        clientService.delete(clientId);
        return ResponseHandler.successResponse(null, uuid.toString());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<Object>> getClient(@PathVariable Long clientId) {
        UUID uuid = UUID.randomUUID();
        var client = clientService.findById(clientId);
        return ResponseHandler.successResponse(client, uuid.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listClients(@RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        UUID uuid = UUID.randomUUID();
        // Validación page/size
        List<ErrorItemDTO> errores = new ArrayList<>();
        final int MAX_SIZE = 100;
        if (page < 0) {
            errores.add(new ErrorItemDTO("E400", "Page must be >= 0", "page"));
        }
        if (size <= 0) {
            errores.add(new ErrorItemDTO("E400", "Size must be > 0", "size"));
        } else if (size > MAX_SIZE) {
            errores.add(new ErrorItemDTO("E400", "Size must be <= " + MAX_SIZE, "size"));
        }
        if (!errores.isEmpty()) {
            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }

        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var pageResult = clientService.findByName(name, pageable);
        return ResponseHandler.successResponse(pageResult, uuid.toString());
    }
}
