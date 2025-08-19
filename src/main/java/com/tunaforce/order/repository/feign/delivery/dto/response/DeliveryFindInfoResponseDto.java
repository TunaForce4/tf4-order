package com.tunaforce.order.repository.feign.delivery.dto.response;

import java.util.UUID;

public record DeliveryFindInfoResponseDto(
        UUID hubId
) {
}
