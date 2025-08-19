package com.tunaforce.order.service;

import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.entity.Order;
import com.tunaforce.order.entity.OrderStatus;
import com.tunaforce.order.repository.feign.auth.AuthFeignClient;
import com.tunaforce.order.repository.feign.delivery.DeliveryFeignClient;
import com.tunaforce.order.repository.feign.product.ProductFeignClient;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import com.tunaforce.order.repository.jpa.OrderJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AuthFeignClient authFeignClient;
    private final ProductFeignClient productFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    private final OrderJpaRepository orderJpaRepository;

    @Transactional
    public void createOrder(OrderCreateRequestDto request, UUID userId) {
        // 유저 역할에 따라 주문 생성에 요청된 업체와 로그인한 유저의 허브 또는 업체와의 관계가 유효한지 검증

        // 주문 생성
        Order order = Order.builder()
                .receiveCompanyId(request.receiveCompanyId())
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
}
