package com.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyServer implements CommandLineRunner {
    @Value("${netty.port}")
    private int port;
    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    public void run(String... args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            //编码 string—>byte
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            //发送出去的消息增加开始标识和结束标识
                            pipeline.addLast(new NettyServerStringWrapHandler());

                            //结束标识 %end%
                            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("%end%", CharsetUtil.UTF_8)));
                            //解码 byte->string
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            //开始标识
                            pipeline.addLast(new NettyServerSubstringHandler());
                            //具体处理
                            pipeline.addLast(nettyServerHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
