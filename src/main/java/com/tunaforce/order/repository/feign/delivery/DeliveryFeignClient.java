package com.tunaforce.order.repository.feign.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "delivery-service",
        url = "localhost:3370",
        path = "/internal/deliveries",
        fallbackFactory = DeliveryFeignFallbackFactory.class
)
public interface DeliveryFeignClient {

    @PostMapping("/order-delivery/create-delivery")
    void createOrderDelivery();
}
