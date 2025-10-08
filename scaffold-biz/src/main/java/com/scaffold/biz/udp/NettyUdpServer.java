package com.scaffold.biz.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;


/**
 * netty-udp服务器
 * 需要确保被其他class调用时已经初始化好，否则可能报错
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyUdpServer implements ApplicationRunner, DisposableBean {

    private volatile boolean running = false;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @Override
    public void destroy() {
        stopServer();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startServer();
    }

    /**
     * 向外发送udp消息
     *
     * @param host 主机
     * @param port 端口
     * @param buf  消息体
     */
    public void send(String host, int port, ByteBuf buf) {
        if (!running) {
            log.error("can not send due to the netty udp server");
        }
        DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress(host, port));
        channel.writeAndFlush(packet);
    }

    /**
     * 重启服务器
     */
    public void restart() {
        stopServer();
        startServer();
    }

    public synchronized void startServer() {
        if (running) {
            log.info("UDP服务已启动");
            return;
        }
        try {
            // 业务线程池，处理业务逻辑
            // Netty工作线程组，处理网络IO
            workerGroup = new NioEventLoopGroup(4);
            Bootstrap bootstrap = new Bootstrap().group(workerGroup).channel(NioDatagramChannel.class)// 设置通道类型为UDP
                    .option(ChannelOption.SO_BROADCAST, true)// 设置广播
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)// 设置接收缓冲区大小
                    .handler(new ChannelInitializer<NioDatagramChannel>() {// 设置ChannelInitializer
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            //todo 使用new是否会有问题
                            ch.pipeline().addLast(new UdpServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(1010).sync();
            channel = future.channel();
            running = true;
            log.info("Netty UDP服务器启动成功，监听端口: {}", 1010);
        } catch (InterruptedException e) {
            log.error("服务器启动被中断", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("UDP服务器启动失败", e);
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }

    public synchronized void stopServer() {
        if (!running) {
            log.info("UDP服务已停止");
            return;
        }
        running = false;
        try {
            if (channel != null) {
                channel.close().sync();
            }

            if (workerGroup != null) {
                workerGroup.shutdownGracefully().sync();
            }

            log.info("Netty UDP服务器已关闭");
        } catch (InterruptedException e) {
            log.error("服务器停止被中断", e);
            Thread.currentThread().interrupt();
        }
    }


}