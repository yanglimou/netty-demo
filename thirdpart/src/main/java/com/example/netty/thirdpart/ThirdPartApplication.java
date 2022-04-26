package com.example.netty.thirdpart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
@Slf4j
public class ThirdPartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdPartApplication.class, args);
    }

    @GetMapping("/heartbeat")
    public String heartbeat(@RequestParam("id") String id) {
        return "success";
    }

    @Value("${api.url}")
    private String url;

    @GetMapping("/fingerprint")
    public String fingerprint(@RequestParam("id") String id) {
        log.info("fingerprint request,id：{}", id);
        RestTemplate restTemplate = new RestTemplate();
        log.info("请求中间件");
        String result;
        try {
            result = restTemplate.getForObject(url + "?id=" + id, String.class);
        } catch (Exception exception) {
            log.error("请求第三方api失败", exception);
            result = "error";
        }
        log.info("中间件返回结果：{}", result);
        return result;
    }

}
