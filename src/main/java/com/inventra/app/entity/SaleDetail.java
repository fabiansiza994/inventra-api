package com.inventra.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class SaleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sale_id_id")
    private Sale saleId;
    @ManyToOne
    @JoinColumn(name = "product_id_id")
    private Product productId;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
}
