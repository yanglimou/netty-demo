package com.example.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        if (msg != null && msg.startsWith("%start%")) {
            String message = msg.substring(7);
            log.info("收到中间件消息：{}", channelHandlerContext.channel().remoteAddress(), message);
            if (message.startsWith("fingerprint")) {//心跳请求
                log.info("收到中间件指纹下发指令");
                String result = "%start%" + message + ":" + (new Random().nextDouble() > 0.5 ? "success" : "error") + "%end%";
                log.info("返回中间件执行下发指令操纵结果：{}", result);
                channelHandlerContext.writeAndFlush(result);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
