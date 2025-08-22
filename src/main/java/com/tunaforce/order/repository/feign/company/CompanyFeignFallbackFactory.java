package com.tunaforce.order.repository.feign.company;


import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyFeignFallbackFactory implements FallbackFactory<CompanyFeignClient> {

    @Override
    public CompanyFeignClient create(Throwable cause) {
        return new CompanyFeignClient() {
            @Override
            public CompanyFindInfoResponseDto findCompanyInfoByUserId(UUID userId) {
                return null;
            }

            @Override
            public CompanyFindInfoResponseDto findCompanyInfoByCompanyId(UUID companyId) {
                return null;
            }

            @Override
            public CompanyFindInfoListResponseDto findCompanyInfoListByHubId(String hubId) {
                return null;
            }
        };
    }
}
