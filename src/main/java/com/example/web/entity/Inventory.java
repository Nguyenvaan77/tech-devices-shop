package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    private Long productId;

    private Integer quantity;

    private Integer reserved;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}