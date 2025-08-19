package com.tunaforce.order.entity;

import com.tunaforce.order.common.exception.CustomRuntimeException;
import com.tunaforce.order.common.exception.OrderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    MASTER("MASTER"),
    COMPANY("COMPANY"),
    HUB("HUB"),
    DELIVERY("DELIVERY"),
    ;

    private final String roleName;

    public static UserRole of(String roleName) {
        return Stream.of(UserRole.values())
                .filter(role -> role.roleName.equalsIgnoreCase(roleName))
                .findFirst().orElseThrow(() -> new CustomRuntimeException(OrderException.ACCESS_DENIED));
    }
}
