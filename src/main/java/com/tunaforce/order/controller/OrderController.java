package com.tunaforce.order.controller;

import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.dto.request.OrderUpdateRequestDto;
import com.tunaforce.order.dto.response.OrderDeleteResponseDto;
import com.tunaforce.order.dto.response.OrderFindDetailResponseDto;
import com.tunaforce.order.dto.response.OrderFindPageResponseDto;
import com.tunaforce.order.entity.SortType;
import com.tunaforce.order.entity.UserRole;
import com.tunaforce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @RequestBody OrderCreateRequestDto orderCreateRequestDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);

        orderService.createOrder(orderCreateRequestDto, userId, role);

        return ResponseEntity.created(null)
                .body(null);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderFindDetailResponseDto> findOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);

        OrderFindDetailResponseDto data = orderService.findOrderDetails(orderId, userId, role);

        return ResponseEntity.ok()
                .body(data);
    }

    @GetMapping("/hubs/{hubId}")
    public ResponseEntity<OrderFindPageResponseDto> findHubOrders(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable UUID hubId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);
        SortType.validate(pageable.getSort());

        OrderFindPageResponseDto data = orderService.findHubOrderPage(pageable, hubId, userId, role);

        return ResponseEntity.ok()
                .body(data);
    }

    @GetMapping("/companies/{companyId}")
    public ResponseEntity<OrderFindPageResponseDto> findCompanyOrders(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable UUID companyId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);
        SortType.validate(pageable.getSort());

        OrderFindPageResponseDto data = orderService.findCompanyOrderPage(pageable, companyId, userId, role);

        return ResponseEntity.ok()
                .body(data);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderUpdateRequestDto orderUpdateRequestDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);
        orderService.updateOrder(orderId, orderUpdateRequestDto, userId, role);

        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);
        orderService.cancelOrder(orderId, userId, role);

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDeleteResponseDto> deleteOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);
        OrderDeleteResponseDto data = orderService.deleteOrder(orderId, userId, role);

        return ResponseEntity.ok()
                .body(data);
    }
}
