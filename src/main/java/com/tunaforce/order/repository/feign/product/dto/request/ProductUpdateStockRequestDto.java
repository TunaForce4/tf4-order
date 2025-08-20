package com.tunaforce.order.repository.feign.product.dto.request;

public record ProductUpdateStockRequestDto(
        Integer orderQuantity,
        Integer updateQuantity
) {
}
