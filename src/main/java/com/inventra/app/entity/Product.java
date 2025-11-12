package com.inventra.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(columnDefinition = "TEXT")
    private String image;
    private Double price;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String brand;
    private String status;
}
