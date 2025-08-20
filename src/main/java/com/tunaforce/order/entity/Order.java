package com.tunaforce.order.entity;

import com.tunaforce.order.common.entity.Timestamped;
import com.tunaforce.order.dto.request.OrderUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "supply_company_id")
    private UUID supplyCompanyId;

    @Column(name = "receive_company_id")
    private UUID receiveCompanyId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "request_memo", length = 200)
    private String requestMemo;

    @Column(name = "order_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder
    public Order(UUID supplyCompanyId, UUID receiveCompanyId, UUID productId, Integer price, Integer quantity, String requestMemo, OrderStatus status) {
        this.supplyCompanyId = supplyCompanyId;
        this.receiveCompanyId = receiveCompanyId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.requestMemo = requestMemo;
        this.status = status;
    }

    public void setProductInfo(UUID supplyCompanyId, Integer price) {
        this.supplyCompanyId = supplyCompanyId;
        this.price = price;
    }

    public void update(OrderUpdateRequestDto requestDto) {
        this.quantity = requestDto.orderQuantity();
        this.requestMemo = requestDto.requestMemo();
    }

    public boolean isBeforeShipping() {
        return this.status.getIndex() < OrderStatus.SHIPPING.getIndex();
    }

    public boolean isDeletableStatus() {
        return this.status.equals(OrderStatus.DELIVERED) || this.status.equals(OrderStatus.CANCELLED);
    }

    // set order status
    public void setStatusPrepared() {
        this.status = OrderStatus.PREPARED;
    }

    public void setStatusPaid() {
        this.status = OrderStatus.PAID;
    }

    public void setStatusReadyForShipment() {
        this.status = OrderStatus.READY_FOR_SHIPMENT;
    }

    public void setStatusShipping() {
        this.status = OrderStatus.SHIPPING;
    }

    public void setStatusDelivered() {
        this.status = OrderStatus.DELIVERED;
    }

    public void setStatusCancelled() {
        this.status = OrderStatus.CANCELLED;
    }
}
