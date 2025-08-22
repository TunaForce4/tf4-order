package com.tunaforce.order.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.tunaforce.order.repository.feign")
public class FeignConfig {

}
