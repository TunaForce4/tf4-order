package com.tunaforce.order.repository.feign.product;

import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "product-service",
        url = "localhost:3390",
        path = "/internal/products",
        fallbackFactory = ProductFeignFallbackFactory.class
)
public interface ProductFeignClient {

    @PatchMapping("/{productId}/order-product/decrease-stock")
    ProductReduceStockResponseDto reduceStock(
            @PathVariable UUID productId,
            @RequestBody ProductReduceStockRequestDto productReduceStockRequestDto,
            @RequestHeader("X-USER-ID") UUID userId
    );
}