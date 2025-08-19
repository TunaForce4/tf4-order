package com.tunaforce.order.controller;

import com.tunaforce.order.dto.request.OrderCreateRequestDto;
import com.tunaforce.order.entity.UserRole;
import com.tunaforce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
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
            @RequestHeader("X-Auth-User-Id") UUID userId, // FIXME 임시
            @RequestHeader("X-Auth-Roles") String userRole
    ) {
        UserRole role = UserRole.of(userRole);

        orderService.createOrder(orderCreateRequestDto, userId, role);

        return ResponseEntity.created(null)
                .body(null);
    }
}
