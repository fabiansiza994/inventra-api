package com.inventra.app.config.exceptions;

import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomServiceException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(CustomServiceException e) {
        ErrorItemDTO err = new ErrorItemDTO(e.getCodError(), e.getDescError(), e.getMessage());
        return ResponseHandler.badRequestResponse(Collections.singletonList(err), e.getIdTx());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        ErrorItemDTO err = new ErrorItemDTO(
                "ERROR", e.getMessage(), "Error in service!"
        );
        return ResponseHandler.internalServerResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ApiResponse<String>> handleInternalServerException(InternalServerException e) {
        ErrorItemDTO err = new ErrorItemDTO(
                "ERROR", e.getMessage(), e.getDescError()
        );
        return ResponseHandler.internalServerResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorized(UnauthorizedException e) {
        ErrorItemDTO err = new ErrorItemDTO(
                "UNAUTHORIZED", e.getMessage(), "Unauthorized"
        );
        return ResponseHandler.unauthorizedResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<String>> handleForbidden(ForbiddenException e) {
        ErrorItemDTO err = new ErrorItemDTO(
                "FORBIDDEN", e.getMessage(), "Not has permission"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(err), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException e) {
        ErrorItemDTO error = new ErrorItemDTO(
                "FORBIDDEN",
                "Access denied",
                "Not has permission"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(error), null);
    }

    @ExceptionHandler(CustomAccesException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomAccessDenied(CustomAccesException e) {
        ErrorItemDTO error = new ErrorItemDTO(
                "FORBIDDEN",
                "Access denied",
                e.getDescError()
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(error), null);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorItemDTO error = new ErrorItemDTO(
                "FORBIDDEN",
                "Access denied",
                "Not has permission"
        );
        return ResponseHandler.forbiddenResponse(Collections.singletonList(error), null);
    }

}
