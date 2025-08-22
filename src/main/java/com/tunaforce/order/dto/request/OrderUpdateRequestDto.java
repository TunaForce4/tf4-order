package com.tunaforce.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateRequestDto(
        @NotNull(message = "상품 주문 수량을 입력해주세요.") @Min(value = 0, message = "상품 주문 수량은 최소 1 이상이어야 합니다.")
        Integer orderQuantity,

        String requestMemo
) {
}
