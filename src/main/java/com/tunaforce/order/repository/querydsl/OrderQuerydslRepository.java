package com.tunaforce.order.repository.querydsl;

import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderQuerydslRepository {

    /**
     * 허브 담당자용 허브 내 업체들의 주문 목록 조회
     */
    Page<OrderDetailsQuerydslResponseDto> findHubOrderPage(Pageable pageable, List<UUID> companyIds);

    /**
     * 업체 담당자용 업체의 주문 목록 조회
     */
    Page<OrderDetailsQuerydslResponseDto> findCompanyOrderPage(Pageable pageable, UUID companyId);
}
