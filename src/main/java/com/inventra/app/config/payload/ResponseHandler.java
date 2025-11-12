package com.inventra.app.config.payload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseHandler {

    public static <T> ResponseEntity<ApiResponse<T>> successResponse(T response, String idTx) {
        DataResponse dataResponse = new DataResponse(
                idTx, "SUCCESS"
        );
        ApiResponse<T> apiResponse = new ApiResponse<>(dataResponse, response, "Realizado!");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequestResponse(List<ErrorItemDTO> error, String idTx) {
        DataResponse dataResponse = new DataResponse(
                idTx, "ERROR"
        );
        ApiResponse<T> apiResponse = new ApiResponse<>(dataResponse, error);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalServerResponse(List<ErrorItemDTO> error, String idTx) {
        DataResponse dataResponse = new DataResponse(
                idTx, "ERROR"
        );
        ApiResponse<T> apiResponse = new ApiResponse<>(dataResponse, error);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<ApiResponse<T>> unauthorizedResponse(List<ErrorItemDTO> error, String idTx) {
        DataResponse dataResponse = new DataResponse(idTx, "UNAUTHORIZED");
        ApiResponse<T> apiResponse = new ApiResponse<>(dataResponse, error);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED); // 401
    }

    public static <T> ResponseEntity<ApiResponse<T>> forbiddenResponse(List<ErrorItemDTO> error, String idTx) {
        DataResponse dataResponse = new DataResponse(idTx, "FORBIDDEN");
        ApiResponse<T> apiResponse = new ApiResponse<>(dataResponse, error);
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN); // 403
    }
}
