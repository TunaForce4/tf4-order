package com.tunaforce.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy년 MM월 dd일")
        LocalDateTime updatedAt
) {
}
