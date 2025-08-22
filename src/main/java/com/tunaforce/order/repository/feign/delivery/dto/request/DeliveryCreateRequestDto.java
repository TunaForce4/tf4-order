package com.tunaforce.order.repository.feign.delivery.dto.request;

import java.util.UUID;

public record DeliveryCreateRequestDto(
        UUID orderId,
        UUID supplyCompanyId,
        UUID receiveCompanyId
) {
}
