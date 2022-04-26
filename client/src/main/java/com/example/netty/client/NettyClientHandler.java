package com.example.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        log.info("收到中间件消息：{}", message);
        if (message.startsWith("fingerprint")) {//指纹下发
            String result = message + ":success";
            channelHandlerContext.writeAndFlush(result);
            log.debug("发送指纹下发结果消息:{}", result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
