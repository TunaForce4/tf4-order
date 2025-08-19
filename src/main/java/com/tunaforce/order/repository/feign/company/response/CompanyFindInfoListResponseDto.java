package com.tunaforce.order.repository.feign.company.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record CompanyFindInfoListResponseDto(
        List<CompanyFindInfoResponseDto> data
) {

    public Map<UUID, String> toMap() {
        return data.stream()
                .collect(Collectors.toMap(
                        CompanyFindInfoResponseDto::companyId,
                        CompanyFindInfoResponseDto::companyName
                ));
    }
}
