package com.example.netty.client;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@Slf4j
@EnableScheduling
public class NettyClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyClientApplication.class, args);
    }

    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private int port;
    private final ConcurrentHashMap<String, NettyClient> map = new ConcurrentHashMap();


    @GetMapping("/createClient")
    public String createClient(@RequestParam("id") String id) {
        NettyClient nettyClient = map.computeIfAbsent(id, key -> NettyClient.createClient(key, host, port));
        if (nettyClient == null) {
            return "error";
        }
        return "success";
    }

    @GetMapping("/listClient")
    public String listClient() {
        return JSON.toJSONString(map.keys());
    }

    @GetMapping("/stopClient")
    public String stopClient(@RequestParam("id") String id) {
        NettyClient nettyClient = map.remove(id);
        if (nettyClient != null) {
            nettyClient.stopClient();
        }
        return "success";
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void run() {
        map.forEach((key, client) -> {
            client.sendHeartBeat();
        });
    }
}
