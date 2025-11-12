package com.inventra.app.config.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class DataResponse {
    private String idTx;
    private String response;
}
