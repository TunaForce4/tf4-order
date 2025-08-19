package com.tunaforce.order.repository.feign.product;

import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "products",
        url = "localhost:3390",
        path = "/internal/products/order-product",
        fallbackFactory = ProductFeignFallbackFactory.class
)
public interface ProductFeignClient {

    @GetMapping("/find-by-id/{productId}")
    ProductFindInfoResponseDto findById(@PathVariable("productId") UUID productId);

    @PatchMapping("/{productId}/decrease-stock")
    ProductReduceStockResponseDto reduceStock(
            @PathVariable UUID productId,
            @RequestBody ProductReduceStockRequestDto productReduceStockRequestDto,
            @RequestHeader("X-USER-ID") UUID userId
    );
}