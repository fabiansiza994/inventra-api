package com.inventra.app.config.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class ErrorItemDTO {
    private String codError;
    private String descError;
    private String msgError;
}
