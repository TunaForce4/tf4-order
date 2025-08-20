package com.tunaforce.order.service;

import com.tunaforce.order.common.exception.CustomRuntimeException;
import com.tunaforce.order.common.exception.OrderException;
import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.dto.response.OrderFindPageResponseDto;
import com.tunaforce.order.entity.Order;
import com.tunaforce.order.entity.OrderStatus;
import com.tunaforce.order.entity.UserRole;
import com.tunaforce.order.repository.feign.auth.AuthFeignClient;
import com.tunaforce.order.repository.feign.company.CompanyFeignClient;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import com.tunaforce.order.repository.feign.delivery.DeliveryFeignClient;
import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import com.tunaforce.order.repository.feign.hub.HubFeignClient;
import com.tunaforce.order.repository.feign.hub.response.HubFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.ProductFeignClient;
import com.tunaforce.order.repository.feign.product.dto.request.ProductFindInfoListRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import com.tunaforce.order.repository.jpa.OrderJpaRepository;
import com.tunaforce.order.repository.querydsl.OrderQuerydslRepository;
import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final HubFeignClient hubFeignClient;
    private final AuthFeignClient authFeignClient;
    private final CompanyFeignClient companyFeignClient;
    private final ProductFeignClient productFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    private final OrderJpaRepository orderJpaRepository;
    private final OrderQuerydslRepository orderQuerydslRepository;

    @Transactional
    public void createOrder(OrderCreateRequestDto request, UUID userId, UserRole role) {
        // 유저 역할에 따라 주문 생성에 요청된 업체와 로그인한 유저의 허브 또는 업체와의 관계가 유효한지 검증
        // 허브 담당자일 경우 - 주문하려는 업체가 소속 업체인지 검증
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByUserId(userId);
            ProductFindInfoResponseDto productInfo = productFeignClient.findById(request.productId());
            validateUuidMatch(hubInfo.hubId(), productInfo.hubId());
        }

        // 업체 담당자의 경우 - 주문하려는 업체가 담당 업체인지 확인
        if (role.equals(UserRole.COMPANY)) {
            CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByUserId(userId);
            ProductFindInfoResponseDto productInfo = productFeignClient.findById(request.productId());
            validateUuidMatch(companyInfo.companyId(), productInfo.companyId());
        }

        // 배송 담당자의 경우 - 주문하려는 업체가 본인 소속 허브의 소속 업체인지 확인
        if (role.equals(UserRole.DELIVERY)) {
            DeliveryFindInfoResponseDto deliveryInfo = deliveryFeignClient.findDeliveryInfoByUserId(userId);
            ProductFindInfoResponseDto productInfo = productFeignClient.findById(request.productId());
            validateUuidMatch(deliveryInfo.hubId(), productInfo.hubId());
        }

        // 주문 생성
        Order order = Order.builder()
                .receiveCompanyId(request.receiveCompanyId())
                .productId(request.productId())
                .quantity(request.orderQuantity())
                .requestMemo(request.requestMemo())
                .status(OrderStatus.PENDING)
                .build();

        // 재고 차감
        ProductReduceStockResponseDto productResponse = productFeignClient.reduceStock(
                request.productId(),
                new ProductReduceStockRequestDto(request.orderQuantity()),
                userId
        );

        order.setProductInfo(productResponse.supplyCompanyId(), productResponse.price());
        order.setStatusPrepared();

        // 결제(가정)
        order.setStatusPaid();

        // 배달 생성
        deliveryFeignClient.createOrderDelivery();
        order.setStatusShipping();

        // 주문 영속화
        orderJpaRepository.save(order);
    }

    /**
     * 특정 허브 소속 업체들의 주문 내역 조회
     */
    public OrderFindPageResponseDto findHubOrderPage(
            Pageable pageable,
            UUID hubId,
            UUID userId,
            UserRole role
    ) {
        validateHubOrdersByAuthority(hubId, userId, role);

        // 조회하려는 허브 정보 조회 및 소속 업체 조회
        HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByHubId(hubId);
        CompanyFindInfoListResponseDto companyInfos = companyFeignClient.findCompanyInfoByHubId(hubId);

        List<UUID> companyIds = companyInfos.data().stream()
                .map(CompanyFindInfoResponseDto::companyId)
                .toList();

        Page<OrderDetailsQuerydslResponseDto> page = orderQuerydslRepository.findHubOrderPage(pageable, companyIds);

        Map<UUID, String> companies = companyInfos.toMap();

        // 상품 id들에 해당하는 상품명 조회
        Set<UUID> productIds = getUniqueProductIds(page.getContent());

        ProductFindInfoListResponseDto productInfoList
                = productFeignClient.findByIds(new ProductFindInfoListRequestDto(productIds.stream().toList()));

        Map<UUID, String> products = productInfoList.toMap();

        return OrderFindPageResponseDto.from(page, hubInfo.hubName(), companies, products);
    }

    public OrderFindPageResponseDto findCompanyOrderPage(Pageable pageable, UUID companyId, UUID userId, UserRole role) {
        validateCompanyOrdersByAuthority(companyId, userId, role);

        Page<OrderDetailsQuerydslResponseDto> page
                = orderQuerydslRepository.findCompanyOrderPage(pageable, companyId);

        // 조회하려는 허브 정보 및 소속 업체, 상품 정보 조회
        CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByCompanyId(companyId);
        HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByHubId(companyInfo.hubId());

        Set<UUID> productIds = getUniqueProductIds(page.getContent());

        ProductFindInfoListResponseDto productInfoList
                = productFeignClient.findByIds(new ProductFindInfoListRequestDto(productIds.stream().toList()));

        Map<UUID, String> products = productInfoList.toMap();

        return OrderFindPageResponseDto.from(page, hubInfo.hubName(), companyInfo.companyName(), products);
    }

    /**
     * Hub 주문 목록 조회 인가
     */
    private void validateHubOrdersByAuthority(UUID hubId, UUID userId, UserRole role) {
        // 권한 - 마스터 또는 본인 허브만 조회 가능
        // 배송 담당자 또는 업체 담당자의 경우 - 허브 주문 내역 조회 불가
        if (role.equals(UserRole.DELIVERY) || role.equals(UserRole.COMPANY)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }

        // 허브 담당자의 경우 - 조회하려는 허브가 본인의 허브인지 확인
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByUserId(userId);
            validateUuidMatch(hubInfo.hubId(), hubId);
        }
    }

    /**
     * Company 주문 목록 조회 인가
     */
    private void validateCompanyOrdersByAuthority(UUID companyId, UUID userId, UserRole role) {
        // 권한 - 마스터 또는 소속 허브 담당자, 본인 업체만 조회 가능
        if (role.equals(UserRole.DELIVERY)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }

        // 허브 담당자의 경우 - 본인의 조회하려는 업체가 본인 허브의 소속 업체인지 확인
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByUserId(userId);
            CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByCompanyId(companyId);
            validateUuidMatch(hubInfo.hubId(), companyInfo.hubId());
        }

        // 업체 담당자의 경우 - 조회하려는 허브가 본인의 허브인지 확인
        if (role.equals(UserRole.COMPANY)) {
            CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByUserId(userId);
            validateUuidMatch(companyId, companyInfo.companyId());
        }
    }

    private void validateUuidMatch(UUID expectedId, UUID actualId) {
        if (!expectedId.equals(actualId)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }
    }

    private Set<UUID> getUniqueProductIds(List<OrderDetailsQuerydslResponseDto> data) {
        return data.stream()
                .map(OrderDetailsQuerydslResponseDto::productId)
                .collect(Collectors.toSet());
    }
}
