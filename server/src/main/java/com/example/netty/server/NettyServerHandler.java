package com.example.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    @Value("${api.url}")
    private String url;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        log.info("收到客户端消息：{}", message);
        if (message.startsWith("heartbeat")) {//心跳请求
            String id = message.substring(10);
            ChannelContainer.put(id, channelHandlerContext.channel());
            RestTemplate restTemplate = new RestTemplate();
            String result;
            try {
                result = restTemplate.getForObject(url + "?id=" + id, String.class);
            } catch (Exception exception) {
                log.error("请求第三方api失败", exception);
                result = "error";
            }
            channelHandlerContext.writeAndFlush("heartbeat:" + result);
        } else if (message.startsWith("fingerprint")) {
            String[] fingerprintResponse = message.split(":");
            String id = fingerprintResponse[1];
            String result = fingerprintResponse[2];
            ResponseContainer.put(id, result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
