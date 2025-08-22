package com.tunaforce.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunaforce.order.entity.OrderStatus;
import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record OrderFindDetailResponseDto(
        UUID orderId,

        String hubName,

        String receiveCompanyName,

        String productName,

        Integer orderQuantity,

        Integer orderPrice,

        String requestMemo,

        OrderStatus orderStatus,

        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDateTime updatedAt
) {

    public static OrderFindDetailResponseDto from(
            OrderDetailsQuerydslResponseDto data,
            String receiveCompanyName,
            String productName,
            String hubName
    ) {
        return OrderFindDetailResponseDto.builder()
                .orderId(data.orderId())
                .hubName(hubName)
                .receiveCompanyName(receiveCompanyName)
                .productName(productName)
                .orderQuantity(data.orderQuantity())
                .orderPrice(data.orderPrice())
                .requestMemo(data.requestMemo())
                .orderStatus(data.status())
                .createdAt(data.createdAt())
                .updatedAt(data.updatedAt())
                .build();
    }
}
