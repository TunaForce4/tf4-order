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
        name = "products",
        path = "/internal/products/order-product",
        fallbackFactory = ProductFeignFallbackFactory.class
)
public interface ProductFeignClient {

    @GetMapping("/find-by-id/{productId}")
    ProductFindInfoResponseDto findById(@PathVariable("productId") UUID productId);

    @PostMapping("/find-by-ids")
    ProductFindInfoListResponseDto findByIds(
            @RequestBody ProductFindInfoListRequestDto productFindInfoListRequestDto
    );

    @PatchMapping("/{productId}/decrease-stock")
    ProductReduceStockResponseDto reduceStock(
            @PathVariable UUID productId,
            @RequestBody ProductReduceStockRequestDto productReduceStockRequestDto,
            @RequestHeader("X-User-Id") UUID userId
    );

    @PatchMapping("/{productId}/update-stock")
    ProductUpdateStockResponseDto updateStock(
            @PathVariable UUID productId,
            @RequestBody ProductUpdateStockRequestDto productUpdateStockRequestDto,
            @RequestHeader("X-User-Id") UUID userId
    );
}