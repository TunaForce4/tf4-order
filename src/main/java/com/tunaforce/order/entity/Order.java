package com.tunaforce.order.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_order")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "request_memo", length = 200)
    private String requestMemo;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
