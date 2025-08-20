package com.tunaforce.order.service;

import com.tunaforce.order.common.exception.CustomRuntimeException;
import com.tunaforce.order.common.exception.OrderException;
import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.dto.request.OrderUpdateRequestDto;
import com.tunaforce.order.dto.response.OrderDeleteResponseDto;
import com.tunaforce.order.dto.response.OrderFindDetailResponseDto;
import com.tunaforce.order.dto.response.OrderFindPageResponseDto;
import com.tunaforce.order.entity.Order;
import com.tunaforce.order.entity.OrderStatus;
import com.tunaforce.order.entity.UserRole;
import com.tunaforce.order.repository.feign.company.CompanyFeignClient;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.company.response.CompanyFindInfoResponseDto;
import com.tunaforce.order.repository.feign.delivery.DeliveryFeignClient;
import com.tunaforce.order.repository.feign.delivery.dto.request.DeliveryCreateRequestDto;
import com.tunaforce.order.repository.feign.delivery.dto.response.DeliveryFindInfoResponseDto;
import com.tunaforce.order.repository.feign.hub.HubFeignClient;
import com.tunaforce.order.repository.feign.hub.response.HubFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.ProductFeignClient;
import com.tunaforce.order.repository.feign.product.dto.request.ProductFindInfoListRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductUpdateStockRequestDto;
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
    private final CompanyFeignClient companyFeignClient;
    private final ProductFeignClient productFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    private final OrderJpaRepository orderJpaRepository;
    private final OrderQuerydslRepository orderQuerydslRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public void createOrder(OrderCreateRequestDto request, UUID userId, UserRole role) {
        validateCreateOrderByAuthority(request.productId(), request.receiveCompanyId(), userId, role);

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

        orderJpaRepository.save(order);

        // 배달 생성
        deliveryFeignClient.createOrderDelivery(new DeliveryCreateRequestDto(
                order.getId(),
                order.getSupplyCompanyId(),
                order.getReceiveCompanyId()
        ));

        order.setStatusReadyForShipment();
    }

    /**
     * 주문 단건 조회
     */
    public OrderFindDetailResponseDto findOrderDetails(UUID orderId, UUID userId, UserRole role) {
        OrderDetailsQuerydslResponseDto orderDetails = orderQuerydslRepository.findOrder(orderId);

        validateFindOrderByAuthority(orderDetails.receiveCompanyId(), orderDetails.createdBy(), userId, role);

        ProductFindInfoResponseDto productInfo = productFeignClient.findByProductId(orderDetails.productId());
        CompanyFindInfoResponseDto companyInfo
                = companyFeignClient.findCompanyInfoByCompanyId(orderDetails.receiveCompanyId());

        return OrderFindDetailResponseDto.from(orderDetails, companyInfo.companyName(), productInfo.productName());
    }

    /**
     * 특정 허브 소속 업체들의 주문 내역 조회
     */
    public OrderFindPageResponseDto findHubOrderPage(Pageable pageable, UUID hubId, UUID userId, UserRole role) {
        validateFindHubOrderPageByAuthority(hubId, userId, role);

        // 조회하려는 허브 정보 조회 및 소속 업체 조회
        HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByHubId(hubId);
        CompanyFindInfoListResponseDto companyInfos = companyFeignClient.findCompanyInfoListByHubId(hubId);

        List<UUID> companyIds = companyInfos.data().stream()
                .map(CompanyFindInfoResponseDto::companyId)
                .toList();

        Page<OrderDetailsQuerydslResponseDto> page = orderQuerydslRepository.findHubOrderPage(pageable, companyIds);

        Map<UUID, String> companies = companyInfos.toMap();

        // 상품 id들에 해당하는 상품명 조회
        Set<UUID> productIds = getUniqueProductIds(page.getContent());

        ProductFindInfoListResponseDto productInfoList
                = productFeignClient.findByProductIds(ProductFindInfoListRequestDto.from(productIds.stream().toList()));

        Map<UUID, String> products = productInfoList.toMap();

        return OrderFindPageResponseDto.from(page, hubInfo.hubName(), companies, products);
    }

    /**
     * 특정 업체 주문 내역 조회
     */
    public OrderFindPageResponseDto findCompanyOrderPage(Pageable pageable, UUID companyId, UUID userId, UserRole role) {
        validateFindCompanyOrderPageByAuthority(companyId, userId, role);

        Page<OrderDetailsQuerydslResponseDto> page
                = orderQuerydslRepository.findCompanyOrderPage(pageable, companyId);

        // 조회하려는 허브 정보 및 소속 업체, 상품 정보 조회
        CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByCompanyId(companyId);
        HubFindInfoResponseDto hubInfo = hubFeignClient.findHubInfoByHubId(companyInfo.hubId());

        Set<UUID> productIds = getUniqueProductIds(page.getContent());

        ProductFindInfoListResponseDto productInfoList
                = productFeignClient.findByProductIds(ProductFindInfoListRequestDto.from(productIds.stream().toList()));

        Map<UUID, String> products = productInfoList.toMap();

        return OrderFindPageResponseDto.from(page, hubInfo.hubName(), companyInfo.companyName(), products);
    }

    /**
     * 주문 수정
     */
    @Transactional
    public void updateOrder(UUID orderId, OrderUpdateRequestDto request, UUID userId, UserRole role) {
        Order order = findById(orderId);

        if (order.isBeforeShipping()) {
            throw new CustomRuntimeException(OrderException.CANNOT_UPDATE_ON_SHIPPING);
        }

        validateUpdateOrDeleteOrderByAuthority(order.getReceiveCompanyId(), userId, role);

        Integer orderQuantity = order.getQuantity();
        Integer updateQuantity = request.orderQuantity();

        // 재고 변경
        if (!orderQuantity.equals(updateQuantity)) {
            productFeignClient.updateStock(
                    order.getProductId(),
                    new ProductUpdateStockRequestDto(orderQuantity, updateQuantity),
                    userId
            );
        }

        order.update(request);
    }

    /**
     * 주문 취소 메인 서비스 로직
     */
    @Transactional
    public void cancelOrder(UUID orderId, UUID userId, UserRole role) {
        Order order = findById(orderId);

        // 배송 진행전까지만 취소 가능
        if (order.isBeforeShipping()) {
            throw new CustomRuntimeException(OrderException.CANNOT_CANCEL_ON_SHIPPING);
        }

        validateUpdateOrDeleteOrderByAuthority(order.getReceiveCompanyId(), userId, role);

        // 재고 변경(복구)
        productFeignClient.updateStock(
                order.getProductId(),
                new ProductUpdateStockRequestDto(order.getQuantity(), 0),
                userId
        );

        order.setStatusCancelled();
    }

    /**
     * 주문 삭제 (소프트 딜리트 용)
     */
    @Transactional
    public OrderDeleteResponseDto deleteOrder(UUID orderId, UUID userId, UserRole role) {
        Order order = findById(orderId);

        // 배송 완료 또는 주문 취소 상태일 때 삭제 가능
        if (!order.isDeletableStatus()) {
            throw new CustomRuntimeException(OrderException.CANNOT_DELETE_ON_THIS_STATUS);
        }

        validateUpdateOrDeleteOrderByAuthority(order.getReceiveCompanyId(), userId, role);

        order.delete(userId);

        return new OrderDeleteResponseDto(true);
    }

    /**
     * 주문 생성 권한 검증
     */
    private void validateCreateOrderByAuthority(UUID productId, UUID receiveCompanyId, UUID userId, UserRole role) {
        CompanyFindInfoResponseDto requestedCompany = companyFeignClient.findCompanyInfoByCompanyId(receiveCompanyId);
        ProductFindInfoResponseDto requestedProduct = productFeignClient.findByProductId(productId);

        // 수령 업체(주문 업체)와 주문하려는 상품의 등록 업체가 동일할 경우 주문 불가능
        validateOwnProduct(requestedCompany.companyId(), requestedProduct.productId());

        // 허브 담당자일 경우 - 주문하려는 상품의 담당 업체가 소속 업체인지 검증
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto userHub = hubFeignClient.findHubInfoByUserId(userId);
            validateOwnProduct(userHub.hubId(), requestedCompany.hubId());
        }

        // 배송 담당자의 경우 - 주문하려는 업체가 본인 담당 허브의 소속 업체인지 검증
        if (role.equals(UserRole.DELIVERY)) {
            DeliveryFindInfoResponseDto userDelivery = deliveryFeignClient.findDeliveryInfoByUserId(userId);
            validateOwnProduct(userDelivery.hubId(), requestedCompany.hubId());
        }

        // 업체 담당자의 경우 - 주문하려는 업체가 본인의 업체인지 검증
        if (role.equals(UserRole.COMPANY)) {
            CompanyFindInfoResponseDto userCompany = companyFeignClient.findCompanyInfoByUserId(userId);
            validateOwnProduct(userCompany.companyId(), requestedCompany.companyId());
        }
    }

    /**
     * 주문 단건 조회 권한 검증
     */
    private void validateFindOrderByAuthority(UUID receiveCompanyId, UUID createdBy, UUID userId, UserRole role) {
        // 허브 담당자의 경우 본인의 허브 소속 업체들의 주문 내역 조회 가능
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto userHub = hubFeignClient.findHubInfoByUserId(userId);
            CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByCompanyId(receiveCompanyId);
            validateUuidMatch(userHub.hubId(), companyInfo.hubId());
        }

        // 배송 담당자나 업체 담당자의 경우 자신의 주문 내역만 조회 가능
        if (role.equals(UserRole.DELIVERY) || role.equals(UserRole.COMPANY)) {
            validateUuidMatch(userId, createdBy);
        }
    }

    /**
     * Hub 주문 목록 조회 권한 검증
     */
    private void validateFindHubOrderPageByAuthority(UUID hubId, UUID userId, UserRole role) {
        // 권한 - 마스터 또는 본인 허브만 조회 가능
        // 배송 담당자 또는 업체 담당자의 경우 - 허브 주문 내역 조회 불가
        if (role.equals(UserRole.DELIVERY) || role.equals(UserRole.COMPANY)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }

        // 허브 담당자의 경우 - 조회하려는 허브가 본인의 허브인지 확인
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto userHub = hubFeignClient.findHubInfoByUserId(userId);
            validateUuidMatch(userHub.hubId(), hubId);
        }
    }

    /**
     * Company 주문 목록 조회 권한 검증
     */
    private void validateFindCompanyOrderPageByAuthority(UUID companyId, UUID userId, UserRole role) {
        // 권한 - 마스터 또는 소속 허브 담당자, 본인 업체만 조회 가능
        if (role.equals(UserRole.DELIVERY)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }

        // 허브 담당자의 경우 - 본인의 조회하려는 업체가 본인 허브의 소속 업체인지 확인
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto userHub = hubFeignClient.findHubInfoByUserId(userId);
            CompanyFindInfoResponseDto companyInfo = companyFeignClient.findCompanyInfoByCompanyId(companyId);
            validateUuidMatch(userHub.hubId(), companyInfo.hubId());
        }

        // 업체 담당자의 경우 - 조회하려는 허브가 본인의 허브인지 확인
        if (role.equals(UserRole.COMPANY)) {
            CompanyFindInfoResponseDto userCompany = companyFeignClient.findCompanyInfoByUserId(userId);
            validateUuidMatch(userCompany.companyId(), companyId);
        }
    }

    /**
     * 주문 수정/삭제용 권한 검증
     */
    private void validateUpdateOrDeleteOrderByAuthority(UUID receiveCompanyId, UUID userId, UserRole role) {
        // 주문 수정/삭제는 마스터, 허브만 가능
        if (role.equals(UserRole.DELIVERY) || role.equals(UserRole.COMPANY)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }

        // 허브 담당자의 경우 - 본인의 조회하려는 업체가 본인 허브의 소속 업체인지 확인
        if (role.equals(UserRole.HUB)) {
            HubFindInfoResponseDto userHub = hubFeignClient.findHubInfoByUserId(userId);
            CompanyFindInfoResponseDto receiveCompany = companyFeignClient.findCompanyInfoByCompanyId(receiveCompanyId);
            validateUuidMatch(userHub.hubId(), receiveCompany.hubId());
        }
    }

    private void validateUuidMatch(UUID expectedId, UUID actualId) {
        if (!expectedId.equals(actualId)) {
            throw new CustomRuntimeException(OrderException.ACCESS_DENIED);
        }
    }

    /**
     * 자신의 허브 또는 업체가 등록한 상품이면 주문 불가능
     */
    private void validateOwnProduct(UUID expectedId, UUID actualId) {
        if (expectedId.equals(actualId)) {
            throw new CustomRuntimeException(OrderException.CANNOT_ORDER_OWN_PRODUCT);
        }
    }

    private Set<UUID> getUniqueProductIds(List<OrderDetailsQuerydslResponseDto> data) {
        return data.stream()
                .map(OrderDetailsQuerydslResponseDto::productId)
                .collect(Collectors.toSet());
    }

    private Order findById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new CustomRuntimeException(OrderException.PRODUCT_NOT_FOUND));
    }
}
