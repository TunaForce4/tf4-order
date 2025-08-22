package com.tunaforce.order.repository.feign.product;

import com.tunaforce.order.common.exception.CustomRuntimeException;
import com.tunaforce.order.common.exception.OrderException;
import com.tunaforce.order.repository.feign.product.dto.request.ProductFindInfoListRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductReduceStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.request.ProductUpdateStockRequestDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoListResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductFindInfoResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductReduceStockResponseDto;
import com.tunaforce.order.repository.feign.product.dto.response.ProductUpdateStockResponseDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ProductFeignFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {

            @Override
            public ProductFindInfoResponseDto findByProductId(UUID productId) {
                return null;
            }

            @Override
            public ProductFindInfoListResponseDto findByProductIds(ProductFindInfoListRequestDto productFindInfoListRequestDto) {
                return null;
            }

            @Override
            public ProductReduceStockResponseDto reduceStock(
                    UUID productId,
                    ProductReduceStockRequestDto productReduceStockRequestDto,
                    UUID userId
            ) {
                if (cause instanceof FeignException.NotFound) {
                    throw new CustomRuntimeException(OrderException.PRODUCT_NOT_FOUND);
                }

                if (cause instanceof FeignException.Conflict) {
                    throw new CustomRuntimeException(OrderException.UNSUFFICIENT_PRODUCT_QUANTITY);
                }

                throw new CustomRuntimeException(OrderException.PRODUCT_SERVICE_UNAVAILABLE);
            }

            @Override
            public ProductUpdateStockResponseDto updateStock(UUID productId, ProductUpdateStockRequestDto productUpdateStockRequestDto, UUID userId) {
                return null;
            }
        };
    }
}
