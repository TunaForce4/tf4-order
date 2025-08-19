package com.tunaforce.order.repository.feign.company.response;

import java.util.UUID;

public record CompanyFindInfoResponseDto(
        UUID companyId
) {
}
