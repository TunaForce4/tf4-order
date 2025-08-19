package com.tunaforce.order.service;

import com.tunaforce.order.common.exception.CustomRuntimeException;
import com.tunaforce.order.common.exception.OrderException;
import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.entity.Order;
import com.tunaforce.order.entity.OrderStatus;
import com.tunaforce.order.entity.UserRole;
import com.tunaforce.order.repository.feign.auth.AuthFeignClient;
import com.tunaforce.order.repository.feign.company.CompanyFeignClient;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import com.tunaforce.order.repository.feign.delivery.DeliveryFeignClient;
import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import com.tunaforce.order.repository.feign.hub.HubFeignClient;
import com.tunaforce.order.repository.feign.hub.response.HubFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.ProductFeignClient;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import com.tunaforce.order.repository.jpa.OrderJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final HubFeignClient hubFeignClient;
    private final AuthFeignClient authFeignClient;
    private final CompanyFeignClient companyFeignClient;
    private final ProductFeignClient productFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    private final OrderJpaRepository orderJpaRepository;

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

    private void validateUuidMatch(UUID expectedId, UUID actualId) {
        if (!expectedId.equals(actualId)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }
    }
}
