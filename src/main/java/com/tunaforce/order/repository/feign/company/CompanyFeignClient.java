package com.tunaforce.order.repository.feign.company;

import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "companies",
        path = "/companies",
        fallbackFactory = CompanyFeignFallbackFactory.class)
public interface CompanyFeignClient {

    @GetMapping("/find-by-user-id/{userId}")
    CompanyFindInfoResponseDto findCompanyInfoByUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/find-by-company-id/{companyId}")
    CompanyFindInfoResponseDto findCompanyInfoByCompanyId(@PathVariable("companyId") UUID companyId);

    @GetMapping("/find-by-hub-id/{hubId}")
    CompanyFindInfoListResponseDto findCompanyInfoListByHubId(@PathVariable("hubId") UUID hubId);
}
