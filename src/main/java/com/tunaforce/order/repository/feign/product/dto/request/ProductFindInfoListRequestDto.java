package com.tunaforce.order.repository.feign.product.dto.request;

import java.util.List;
import java.util.UUID;

public record ProductFindInfoListRequestDto(
        List<UUID> productId
) {
}
