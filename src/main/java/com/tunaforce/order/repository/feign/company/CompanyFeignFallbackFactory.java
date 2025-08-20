package com.tunaforce.order.repository.feign.company;


import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CompanyFeignFallbackFactory implements FallbackFactory<CompanyFeignClient> {

    @Override
    public CompanyFeignClient create(Throwable cause) {
        return null;
    }
}
