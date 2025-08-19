package com.tunaforce.order.repository.feign.hub.response;

import java.util.UUID;

public record HubFindInfoResponseDto(
        UUID hubId,
        String hubName
) {
}
