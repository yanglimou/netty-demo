package com.example.netty.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SpringBootApplication
@RestController
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        return ChannelContainer.list() + ResponseContainer.list();
    }

    @GetMapping("/fingerprint")
    public String fingerprint(@RequestParam("id") String id) {
        log.info("来自第三方的【指纹下发请求】请求内容,id：{}", id);
        Channel channel = ChannelContainer.get(id);
        String result;
        if (channel == null || !channel.isWritable()) {
            result = "设备不存在或者未上线";
            log.error("设备不存在或者未上线");
        } else {
            String uuid = UUID.randomUUID().toString();
            String request = "fingerprint:" + uuid;
            log.info("【指纹下发请求】请求客户端，request：{}", request);
            channel.writeAndFlush(request);
            //阻塞等待结果
            try {
                //阻塞等待结果
                result = ResponseContainer.take(uuid);
                log.info("【指纹下发请求】客户端响应，response：{}", result);
            } catch (Exception e) {
                e.printStackTrace();
                result = "【指纹下发请求】客户端响应超时";
                log.error("【指纹下发请求】客户端响应，response：{}", result);
            }
        }
        return result;
    }
}
