package com.tunaforce.order.dto.response;

import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Builder
public record OrderFindPageResponseDto(
        long totalElements,

        int totalPages,

        int currentPage,

        int currentSize,

        List<OrderFindDetailResponseDto> data
) {

    public static OrderFindPageResponseDto from(
            Page<OrderDetailsQuerydslResponseDto> page,
            String hubName,
            Map<UUID, String> companies,
            Map<UUID, String> products
    ) {
        List<OrderFindDetailResponseDto> data = page.getContent().stream()
                .map(order -> OrderFindDetailResponseDto.builder()
                        .orderId(order.orderId())
                        .hubName(hubName)
                        .receiveCompanyName(companies.get(order.receiveCompanyId()))
                        .productName(products.get(order.productId()))
                        .orderQuantity(order.orderQuantity())
                        .orderPrice(order.orderPrice())
                        .requestMemo(order.requestMemo())
                        .orderStatus(order.status())
                        .createdAt(order.createdAt())
                        .updatedAt(order.updatedAt())
                        .build())
                .toList();

        return OrderFindPageResponseDto.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .currentSize(page.getNumberOfElements())
                .data(data)
                .build();
    }

    public static OrderFindPageResponseDto from(
            Page<OrderDetailsQuerydslResponseDto> page,
            String hubName,
            String companyName,
            Map<UUID, String> products
    ) {
        List<OrderFindDetailResponseDto> data = page.getContent().stream()
                .map(order -> OrderFindDetailResponseDto.builder()
                        .orderId(order.orderId())
                        .hubName(hubName)
                        .receiveCompanyName(companyName)
                        .productName(products.get(order.productId()))
                        .orderQuantity(order.orderQuantity())
                        .orderPrice(order.orderPrice())
                        .requestMemo(order.requestMemo())
                        .orderStatus(order.status())
                        .createdAt(order.createdAt())
                        .updatedAt(order.updatedAt())
                        .build())
                .toList();

        return OrderFindPageResponseDto.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .currentSize(page.getNumberOfElements())
                .data(data)
                .build();
    }
}
