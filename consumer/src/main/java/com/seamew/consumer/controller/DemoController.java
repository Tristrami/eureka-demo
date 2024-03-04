package com.seamew.consumer.controller;

import com.google.common.base.Strings;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.decorator.EurekaHttpClientDecorator;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.http.RestTemplateEurekaHttpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 相关配置
 * <ul>
 *   <li>{@code register-with-eureka}: 是否将服务信息注册到 eureka</li>
 *   <li>{@code fetch-registry}: 是否要获取配置</li>
 *   <li>{@code registry-fetch-interval-seconds}: 配置获取时间间隔</li>
 * </ul>
 *
 * <p>
 * 如果 {@code fetch-registry} 配置为开启，eureka 启动时，会在
 * {@link com.netflix.discovery.DiscoveryClient#initScheduledTasks initScheduledTasks} 方法中注册一个定时任务线程
 * {@link com.netflix.discovery.DiscoveryClient.CacheRefreshThread CacheRefreshThread} 从 eureka server 获取服务注册表
 * <p>
 * 获取流程:
 * <p>
 * {@link com.netflix.discovery.DiscoveryClient#refreshRegistry refreshRegistry}
 * <p>
 * {@link com.netflix.discovery.DiscoveryClient#fetchRegistry fetchRegistry}
 * <p>
 * 获取全量配置信息
 * <p>
 * {@link com.netflix.discovery.DiscoveryClient#getAndStoreFullRegistry getAndStoreFullRegistry}
 * <p>
 * {@link EurekaHttpClientDecorator#getApplications getApplications}
 * <p>
 * {@link RestTemplateEurekaHttpClient#getApplications getApplications}，发送 /apps 请求
 * <p>
 * {@link RestTemplateEurekaHttpClient#getApplicationsInternal getApplicationsInternal}，使用 {@code restTemplate} 发送请求
 * <p>
 * 获取到的应用信息将存储在 {@link com.netflix.discovery.DiscoveryClient#localRegionApps localRegionApps } 对象中
 * <p>
 * 获取增量配置信息
 * <p>
 * {@link com.netflix.discovery.DiscoveryClient#getAndUpdateDelta getAndUpdateDelta}
 * <p>
 * {@link RestTemplateEurekaHttpClient#getDelta getDelta}
 * <p>
 * {@link RestTemplateEurekaHttpClient#getDelta getDelta}，发送 /apps/delta 请求
 * <p>
 * {@link RestTemplateEurekaHttpClient#getApplicationsInternal getApplicationsInternal}，使用 {@code restTemplate} 发送请求
 * <p>
 * {@link com.netflix.discovery.DiscoveryClient#updateDelta updateDelta} 对本地注册表中的服务实例信息执行增删改操作
 */
@RestController
@Slf4j
public class DemoController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public String test() throws MalformedURLException {

        String responseData = null;
        List<ServiceInstance> instances = discoveryClient.getInstances("provider");
        for (ServiceInstance instance : instances) {
            log.info("服务实例 host: {}, port: {}", instance.getHost(), instance.getPort());
            URL url = new URL("http", instance.getHost(), instance.getPort(), "/test");
            log.info("调用服务开始, url: {}", url);
            HttpResponse<String> response = Unirest.get(url.toString()).asString();
            responseData = response.getBody();
            log.info("服务调用结束, 响应数据: {}", responseData);
        }
        return responseData;
    }
}
