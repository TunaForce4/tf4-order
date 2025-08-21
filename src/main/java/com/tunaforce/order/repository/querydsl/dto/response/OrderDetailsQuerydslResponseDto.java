package com.tunaforce.order.repository.querydsl.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.tunaforce.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDetailsQuerydslResponseDto(
        UUID orderId,

        UUID receiveCompanyId,

        UUID productId,

        Integer orderPrice,

        Integer orderQuantity,

        String requestMemo,

        OrderStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

    @QueryProjection
    public OrderDetailsQuerydslResponseDto {
    }
}
