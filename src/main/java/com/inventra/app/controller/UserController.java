package com.inventra.app.controller;

import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.RegisterDTO;
import com.inventra.app.entity.dto.UserDTO;
import com.inventra.app.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody @Valid RegisterDTO request, BindingResult result) {
        String uuid = UUID.randomUUID().toString();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid);
        }
        var response = userService.registerUser(request, uuid);
        return ResponseHandler.successResponse(response, uuid);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchUsers(@RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        String uuid = UUID.randomUUID().toString();
        // Validación básica de paginación
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
            return ResponseHandler.badRequestResponse(errores, uuid);
        }

        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var usersPage = userService.findByName(name, pageable);
        return ResponseHandler.successResponse(usersPage, uuid);
    }

    // Detalle de usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getUserDetail(@PathVariable Long id) {
        String uuid = UUID.randomUUID().toString();
        var user = userService.findById(id);
        return ResponseHandler.successResponse(user, uuid);
    }

    // Actualizar usuario (sin cambiar password)
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> updateUser(@RequestBody @Valid UserDTO request, BindingResult result) {
        String uuid = UUID.randomUUID().toString();
        List<ErrorItemDTO> errores = new ArrayList<>();
        if (request.getId() == null) {
            errores.add(new ErrorItemDTO("E400", "id is required", "id"));
        }
        if (result.hasErrors()) {
            errores.addAll(result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .toList());
        }
        if (!errores.isEmpty()) {
            return ResponseHandler.badRequestResponse(errores, uuid);
        }
        var updated = userService.update(request, uuid);
        return ResponseHandler.successResponse(updated, uuid);
    }
}
