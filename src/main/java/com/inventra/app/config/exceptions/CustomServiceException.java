package com.inventra.app.config.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CustomServiceException extends RuntimeException{
    private String idTx;
    private String codError;
    private String descError;

    public CustomServiceException(String idTx, String codError, String descError) {
        super(codError);
        this.idTx = idTx;
        this.codError = codError;
        this.descError = descError;
    }
}
