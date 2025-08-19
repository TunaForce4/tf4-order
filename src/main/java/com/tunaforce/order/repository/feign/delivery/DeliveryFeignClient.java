package com.tunaforce.order.repository.feign.delivery;

import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(
        name = "delivery-service",
        url = "localhost:3370",
        path = "/internal/deliveries/order-delivery",
        fallbackFactory = DeliveryFeignFallbackFactory.class
)
public interface DeliveryFeignClient {

    @GetMapping("/find-by-user-id/{userId}")
    DeliveryFindInfoResponseDto findDeliveryInfoById(@PathVariable("userId") UUID userId);

    @PostMapping("/create-delivery")
    void createOrderDelivery();
}
