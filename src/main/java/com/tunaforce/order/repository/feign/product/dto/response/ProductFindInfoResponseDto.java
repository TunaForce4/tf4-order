package com.tunaforce.order.repository.feign.product.dto.response;

import java.util.UUID;

public record ProductFindInfoResponseDto(
        UUID productId,

        UUID companyId,

        UUID hubId,

        String productName
) {
}
