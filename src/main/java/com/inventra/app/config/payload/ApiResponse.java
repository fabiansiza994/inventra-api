package com.inventra.app.config.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{

    private DataResponse dataResponse;
    private T data;
    private List<ErrorItemDTO> error;
    private String message;

    // error
    public ApiResponse(DataResponse dataResponse, List<ErrorItemDTO> error) {
        this.dataResponse = dataResponse;
        this.error = error;
    }

    // success
    public ApiResponse(DataResponse dataResponse, T data, String message) {
        this.dataResponse = dataResponse;
        this.data = data;
        this.message = message;
    }
}
