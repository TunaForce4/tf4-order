package com.tunaforce.order.repository.querydsl;

import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderQuerydslRepository {

    /**
     * 허브 소속 업체들에 대한 페이지네이션
     */
    Page<OrderDetailsQuerydslResponseDto> findHubOrderPage(Pageable pageable, List<UUID> companyIds);

    Page<OrderDetailsQuerydslResponseDto> findCompanyOrderPage(Pageable pageable, UUID companyId);
}
