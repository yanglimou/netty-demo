package com.example.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
    private NioEventLoopGroup workerGroup;

    public void sendHeartBeat() {
        if (channel.isWritable()) {
            channel.writeAndFlush("heartbeat:" + id);
            log.debug("发送心跳信息");
        }
    }

    private String id;

    private Channel channel;

    private NettyClient() {
    }

    public void stopClient() {
        try {
            if (channel != null)
                channel.close();
        } catch (Exception exception) {
            log.error("channel.close 报错", exception);
        } finally {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }

    public static NettyClient createClient(String id, String host, int port) {
        NettyClient nettyClient = new NettyClient();
        nettyClient.id = id;
        //判断是否已经存在，不存在才创建
        nettyClient.workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nettyClient.workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();


                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new NettyClientStringWrapHandler());

                            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("%end%", CharsetUtil.UTF_8)));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new NettyClientSubstringHandler());
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            nettyClient.channel = channelFuture.channel();
            nettyClient.channel.closeFuture().addListener((ChannelFutureListener) channelFuture1 -> {
                nettyClient.workerGroup.shutdownGracefully();
            });
            log.info("client创建成功");
            return nettyClient;
        } catch (Exception exception) {
            log.error("client创建失败", exception);
            nettyClient.workerGroup.shutdownGracefully();
            return null;
        }
    }
}
