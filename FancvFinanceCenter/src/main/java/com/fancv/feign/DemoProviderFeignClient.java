package com.fancv.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "operation-service")
public interface DemoProviderFeignClient {

    @GetMapping("/echo")
    String echo(@RequestParam("name") String name);

}
