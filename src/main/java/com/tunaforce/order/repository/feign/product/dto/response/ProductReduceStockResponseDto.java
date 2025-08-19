package com.tunaforce.order.repository.feign.product.dto.response;

import java.util.UUID;

public record ProductReduceStockResponseDto(
        UUID supplyCompanyId,
        Integer price
) {
}
