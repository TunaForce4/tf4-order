package com.tunaforce.order.repository.feign.company;

import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "company",
        path = "/companies",
        fallbackFactory = CompanyFeignFallbackFactory.class
)
public interface CompanyFeignClient {

    @GetMapping("/users/{userId}")
    CompanyFindInfoResponseDto findCompanyInfoByUserId(
            @PathVariable("userId") UUID userId
    );

    @GetMapping("/{companyId}")
    CompanyFindInfoResponseDto findCompanyInfoByCompanyId(
            @PathVariable("companyId") UUID companyId
    );

    @GetMapping
    CompanyFindInfoListResponseDto findCompanyInfoListByHubId(
            @RequestParam String hubId
    );
}
