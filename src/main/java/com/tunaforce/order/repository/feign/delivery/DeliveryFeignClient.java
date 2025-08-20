package com.tunaforce.order.repository.feign.delivery;

import com.tunaforce.order.repository.feign.delivery.dto.request.DeliveryCreateRequestDto;
import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "deliveries",
        fallbackFactory = DeliveryFeignFallbackFactory.class
)
public interface DeliveryFeignClient {

    @GetMapping("/delivery-agents/{userId}")
    DeliveryFindInfoResponseDto findDeliveryInfoByUserId(@PathVariable("userId") UUID userId);

    @PostMapping("/deliveries")
    void createOrderDelivery(@RequestBody DeliveryCreateRequestDto deliveryCreateRequestDto);
}
