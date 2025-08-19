package com.tunaforce.order.repository.feign.company;

import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "companies",
        url = "localhost:3360",
        path = "/internal/companies/order-company",
        fallbackFactory = CompanyFeignFallbackFactory.class)
public interface CompanyFeignClient {

    @GetMapping("/find-by-user-id/{userId}")
    CompanyFindInfoResponseDto findHubInfoByUserId(@PathVariable("userId") UUID userId);
}
