package com.tunaforce.order.repository.feign.product;

import com.tunaforce.order.repository.feign.product.dto.request.ProductFindInfoListRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductUpdateStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductUpdateStockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "product",
        path = "/products/internal",
        fallbackFactory = ProductFeignFallbackFactory.class
)
public interface ProductFeignClient {

    @GetMapping("/{productId}")
    ProductFindInfoResponseDto findByProductId(
            @PathVariable("productId") UUID productId
    );

    @PostMapping
    ProductFindInfoListResponseDto findByProductIds(
            @RequestBody ProductFindInfoListRequestDto productFindInfoListRequestDto
    );

    @PostMapping("/{productId}/reduce-stock")
    ProductReduceStockResponseDto reduceStock(
            @PathVariable UUID productId,
            @RequestBody ProductReduceStockRequestDto productReduceStockRequestDto,
            @RequestHeader("X-User-Id") UUID userId
    );

    @PostMapping("/{productId}/update-order-quantity")
    ProductUpdateStockResponseDto updateStock(
            @PathVariable UUID productId,
            @RequestBody ProductUpdateStockRequestDto productUpdateStockRequestDto,
            @RequestHeader("X-User-Id") UUID userId
    );
}