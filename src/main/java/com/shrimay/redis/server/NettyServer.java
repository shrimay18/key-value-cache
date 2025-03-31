package com.shrimay.redis.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrimay.redis.service.CacheService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NettyServer implements CommandLineRunner {

    private static final int PORT = 7171;
    private static final int BOSS_THREADS = 2;
    private static final int WORKER_THREADS = 4;
    private static final int BUSINESS_THREADS = 8;

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public NettyServer(CacheService cacheService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean useEpoll = System.getProperty("os.name").toLowerCase().contains("linux");

        EventLoopGroup bossGroup = useEpoll ? new EpollEventLoopGroup(BOSS_THREADS) : new NioEventLoopGroup(BOSS_THREADS);
        EventLoopGroup workerGroup = useEpoll ? new EpollEventLoopGroup(WORKER_THREADS) : new NioEventLoopGroup(WORKER_THREADS);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer(cacheService, objectMapper, new DefaultEventExecutorGroup(BUSINESS_THREADS)));

            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("Netty server started onnnnnn port " + PORT);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
