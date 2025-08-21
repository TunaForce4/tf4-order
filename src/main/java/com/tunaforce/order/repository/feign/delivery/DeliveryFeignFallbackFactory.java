package com.tunaforce.order.repository.feign.delivery;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class DeliveryFeignFallbackFactory implements FallbackFactory<DeliveryFeignClient> {

    @Override
    public DeliveryFeignClient create(Throwable cause) {
        return null;
    }
}
