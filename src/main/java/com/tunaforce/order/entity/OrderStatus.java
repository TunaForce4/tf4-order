package com.tunaforce.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("PENDING", 1), // 주문 요청
    PREPARED("PREPARED", 2), // 상품 준비 완료
    PAID("PAID", 3), // 결제 완료
    READY_FOR_SHIPMENT("READY_FOR_SHIPMENT", 4),
    SHIPPING("SHIPPING", 5), // 배송 중
    DELIVERED("DELIVERED", 6), // 배송 완료

    CANCELLED("CANCELLED", 7), // 주문 취소
    ;

    private final String status;
    private final int index;
}
