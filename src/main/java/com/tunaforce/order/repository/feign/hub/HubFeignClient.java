package com.tunaforce.order.repository.feign.hub;

import com.tunaforce.order.repository.feign.hub.response.HubFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "hubs",
        url = "localhost:3340",
        path = "/internal/hubs/order-hub",
        fallbackFactory = HubFeignFallbackFactory.class)
public interface HubFeignClient {

    @GetMapping("/find-by-user-id/{userId}")
    HubFindInfoResponseDto findHubInfoByUserId(@PathVariable("userId") UUID userId);
}
