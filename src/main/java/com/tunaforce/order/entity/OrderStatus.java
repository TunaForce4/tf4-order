package com.tunaforce.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("PENDING"), // 주문 요청
    PREPARED("PREPARED"), // 상품 준비 완료
    PAID("PAID"), // 결제 완료
    READY_FOR_SHIPMENT("READY_FOR_SHIPMENT"),
    SHIPPING("SHIPPING"), // 배송 중
    DELIVERED("DELIVERED"), // 배송 완료

    CANCELLED("CANCELLED"), // 주문 취소
    ;

    private final String status;
}
