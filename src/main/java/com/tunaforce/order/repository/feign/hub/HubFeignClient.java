package com.tunaforce.order.repository.feign.hub;

import com.tunaforce.order.repository.feign.hub.dto.response.HubFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "hub",
        path = "/hubs",
        fallbackFactory = HubFeignFallbackFactory.class
)
public interface HubFeignClient {

    @GetMapping("/admins/{userId}")
    HubFindInfoResponseDto findHubInfoByUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/{hubId}")
    HubFindInfoResponseDto findHubInfoByHubId(@PathVariable("hubId") UUID hubId);
}
