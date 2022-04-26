package com.example.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        if (msg != null && msg.startsWith("%start%")) {
            String message = msg.substring(7);
            log.info("收到客户端消息：{}", channelHandlerContext.channel().remoteAddress(), message);
            if (message.startsWith("heartbeat")) {//心跳请求
                String id = message.substring(10);
                ChannelContainer.put(id, channelHandlerContext.channel());
                RestTemplate restTemplate = new RestTemplate();
//                log.info("请求第三方");
                String result = restTemplate.getForObject("http://localhost:8080/heartbeat?id=" + id, String.class);
//                log.info("第三方返回结果：{}", result);
//                log.info("返回结果发送客户端");
                channelHandlerContext.writeAndFlush("%start%heartbeat:" + result + "%end%");
            } else if (message.startsWith("fingerprint")) {
                String[] fingerprintResponse = message.split(":");
                String id = fingerprintResponse[1];
                String result = fingerprintResponse[2];
                ResponseContainer.put(id, result);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
