package com.tunaforce.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("PENDING"), // 주문 요청
    PAID("PAID"), // 결제 완료
    PREPARING("PREPARING"), // 상품 준비중
    SHIPPED("SHIPPED"), // 배송 출발
    DELIVERED("DELIVERED"), // 배송 완료

    CANCELLED("CANCELLED"), // 주문 취소
    ;

    private final String status;
}
