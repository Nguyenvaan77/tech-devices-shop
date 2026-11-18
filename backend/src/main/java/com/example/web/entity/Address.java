package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverName;

    private String phone;

    private String province;

    private String district;

    private String ward;

    private String detail;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}