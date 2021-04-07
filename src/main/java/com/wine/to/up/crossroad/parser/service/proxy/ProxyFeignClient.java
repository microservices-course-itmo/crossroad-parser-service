package com.wine.to.up.crossroad.parser.service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


@FeignClient(name = "proxy-service")
public interface ProxyFeignClient {
    @GetMapping("/proxies")
    List<Proxy> getProxies(@RequestParam String serviceName);
}
