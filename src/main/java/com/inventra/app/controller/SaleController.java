package com.inventra.app.controller;

import com.inventra.app.config.payload.ApiResponse;
import com.inventra.app.config.payload.ErrorItemDTO;
import com.inventra.app.config.payload.ResponseHandler;
import com.inventra.app.entity.dto.SalesDto;
import com.inventra.app.service.ISalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final ISalesService salesService;

    public SaleController(ISalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createSale(@RequestBody SalesDto request, BindingResult result) {
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
        var saleInfo = salesService.createSale(request);
        return ResponseHandler.successResponse(saleInfo, uuid.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listSales(@RequestParam(value = "date", required = false) String date,
                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        UUID uuid = UUID.randomUUID();
        // validación básica
        if (page < 0 || size <= 0) {
            var errores = List.of(new ErrorItemDTO("E400", "Invalid pagination params", "page/size"));
            return ResponseHandler.badRequestResponse(errores, uuid.toString());
        }
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var pageResult = salesService.listSales(date, pageable);
        return ResponseHandler.successResponse(pageResult, uuid.toString());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> saleDetail(@PathVariable Long id) {
        UUID uuid = UUID.randomUUID();
        var detail = salesService.getSaleDetail(id);
        return ResponseHandler.successResponse(detail, uuid.toString());
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<Object>> updateStatus(@PathVariable Long id, @PathVariable String status){
        UUID uuid = UUID.randomUUID();
        salesService.updateSaleStatus(id, status);
        return ResponseHandler.successResponse(null, uuid.toString());
    }

}
