package com.tunaforce.order.repository.feign.product.dto.request;

import java.util.List;
import java.util.UUID;

public record ProductFindInfoListRequestDto(
        List<ProductFindInfoRequestDto> productIds
) {

    public static ProductFindInfoListRequestDto from(List<UUID> productIds) {
        return new ProductFindInfoListRequestDto(
                productIds.stream()
                        .map(ProductFindInfoRequestDto::new)
                        .toList()
        );
    }
}
