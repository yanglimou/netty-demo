package com.example.netty.thirdpart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@SpringBootApplication
@RestController
@Slf4j
public class ThirdPartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdPartApplication.class, args);
    }

    @GetMapping("/heartbeat")
    public String heartbeat(@RequestParam("id") String id) {
//        log.info("heartbeat request,id：{}", id);
        return new Random().nextDouble() > 0.5 ? "success" : "error";
    }

    @GetMapping("/fingerprint")
    public String fingerprint(@RequestParam("id") String id) {
        log.info("fingerprint request,id：{}", id);
        RestTemplate restTemplate = new RestTemplate();
        log.info("请求中间件");
        String result = restTemplate.getForObject("http://localhost:8082/fingerprint?id=" + id, String.class);
        log.info("中间件返回结果：{}", result);
        return result;
    }

}
