package com.tunaforce.order.repository.feign.product.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record ProductFindInfoListResponseDto(
        List<ProductFindInfoResponseDto> data
) {

    public Map<UUID, String> toMap() {
        return data.stream()
                .collect(Collectors.toMap(
                        ProductFindInfoResponseDto::productId,
                        ProductFindInfoResponseDto::productName
                ));
    }
}
