package dev.nkucherenko.redischat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import org.jboss.netty.util.VirtualExecutorService;
import org.springframework.boot.autoconfigure.web.embedded.NettyWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

@Configuration
public class AppConfiguration {

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup(1); // Здесь вы можете указать количество потоков
    }

    @Bean
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup(); // По умолчанию будет использоваться количество доступных процессорных ядер
    }

    @Bean
    public ServerBootstrap serverBootstrap(MyChannelInitializer myChannelInitializer) {
        VirtualExecutorService
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(myChannelInitializer); // Используем свой ChannelInitializer
        return b;
    }

    @Bean
    public EventExecutorGroup customEventExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
