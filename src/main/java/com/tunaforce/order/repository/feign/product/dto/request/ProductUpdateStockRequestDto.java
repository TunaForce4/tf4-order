package com.tunaforce.order.repository.feign.product.dto.request;

public record ProductUpdateStockRequestDto(
        Integer originalQuantity,
        Integer updateQuantity
) {
}
