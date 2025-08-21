package com.tunaforce.order.repository.feign.delivery;

import com.tunaforce.order.repository.feign.delivery.dto.request.DeliveryCreateRequestDto;
import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "delivery",
        fallbackFactory = DeliveryFeignFallbackFactory.class
)
public interface DeliveryFeignClient {

    @GetMapping("/delivery-agents")
    DeliveryFindInfoResponseDto findDeliveryInfoByUserId(@RequestParam String q);

    @PostMapping("/deliveries")
    void createOrderDelivery(@RequestBody DeliveryCreateRequestDto deliveryCreateRequestDto);
}
