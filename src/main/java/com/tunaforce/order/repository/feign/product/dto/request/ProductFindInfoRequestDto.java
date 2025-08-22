package com.tunaforce.order.repository.feign.product.dto.request;

import java.util.UUID;

public record ProductFindInfoRequestDto(
        UUID productId
) {
}
