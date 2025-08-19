package com.tunaforce.order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderException {

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),
    UNSUFFICIENT_PRODUCT_QUANTITY(HttpStatus.CONFLICT, "상품 재고가 부족합니다."),
    PRODUCT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스가 동작하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
