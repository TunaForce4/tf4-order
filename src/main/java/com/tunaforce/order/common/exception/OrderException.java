package com.tunaforce.order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderException {

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    CANNOT_ORDER_OWN_PRODUCT(HttpStatus.CONFLICT, "본인 허브 또는 업체의 상품을 주문할 수 없습니다."),
    CANNOT_UPDATE_ON_SHIPPING(HttpStatus.CONFLICT, "배송이 이미 시작된 상태에서는 수정할 수 없습니다."),
    CANNOT_CANCEL_ON_SHIPPING(HttpStatus.CONFLICT, "배송이 이미 시작된 상태에서는 취소할 수 없습니다."),
    CANNOT_DELETE_ON_THIS_STATUS(HttpStatus.CONFLICT, "배송이 완료되거나 취소된 상태에서만 삭제할 수 있습니다."),
    UNSUPPORTED_SORT_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 타입입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),
    UNSUFFICIENT_PRODUCT_QUANTITY(HttpStatus.CONFLICT, "상품 재고가 부족합니다."),
    PRODUCT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스가 동작하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
