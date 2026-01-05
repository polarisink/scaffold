package com.scaffold.udp.server;

import cn.hutool.core.util.StrUtil;
import com.scaffold.udp.IBytes;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;


/**
 * netty-udp服务器
 * 需要确保被其他class调用时已经初始化好，否则可能报错
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UdpServer implements ApplicationRunner, DisposableBean {

    private final UdpProperties udpProperties;
    private volatile boolean running = false;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @Override
    public void destroy() {
        stopServer();
    }

    @Override
    public void run(ApplicationArguments args) {
        startServer();
    }

    public <T extends IBytes> void send(T iBytes) {
        send(iBytes.toBuf(), iBytes.getName());
    }


    public void send(ByteBuf buf, String logName) {
        //不能根据这个做转发
        send(udpProperties.getReceiverHost(), udpProperties.getPort(), buf, logName);
    }

    /**
     * 向外发送udp消息
     *
     * @param host 主机
     * @param port 端口
     * @param buf  消息体
     */
    public void send(String host, int port, ByteBuf buf, String logName) {
        Assert.isTrue(running, "udp服务未启动");
        InetSocketAddress receiver = new InetSocketAddress(host, port);
        //InetSocketAddress sender = new InetSocketAddress(NetUtil.getLocalhostStr(), port);
        ByteBuf copy = buf.copy();
        int length = copy.readableBytes();
        String copyStr = ByteBufUtil.hexDump(copy);
        DatagramPacket packet = new DatagramPacket(buf, receiver);

        ChannelFuture channelFuture = channel.writeAndFlush(packet);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                if (logName != null) {
                    log.info("发送【{}】数据到【{}】成功，长度：{}，数据：{}", logName, receiver, length, copyStr);
                } else {
                    log.info("发送数据到【{}】成功，长度：{}，数据：{}", receiver, length, copyStr);
                }
            } else {
                if (logName != null) {
                    log.error("发送【{}】数据到【{}】失败，原因：{}，长度：{}，数据：{}", logName, receiver, future.cause().getMessage(), length, copyStr);
                } else {
                    log.error("发送数据到【{}】失败，原因：{}，长度：{}，数据：{}", receiver, future.cause().getMessage(), length, copyStr);
                }
            }
        });
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
            log.info("udp服务已启动");
            return;
        }
        try {
            // 业务线程池，处理业务逻辑
            // Netty工作线程组，处理网络IO
            Bootstrap bootstrap = new Bootstrap().channel(NioDatagramChannel.class)// 设置通道类型为UDP
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 5)// 设置接收缓冲区大小
                    .option(ChannelOption.SO_BROADCAST, true)//开启广播
                    .handler(new ChannelInitializer<NioDatagramChannel>() {// 设置ChannelInitializer
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new EvaluateServerHandler());
                        }
                    });
            ChannelFuture future = StrUtil.isBlank(udpProperties.getLocalhost())
                    //不指定ip
                    ? bootstrap.bind(udpProperties.getPort()).sync()
                    //指定ip
                    : bootstrap.bind(udpProperties.getLocalhost(), udpProperties.getPort()).sync();
            //ChannelFuture future = bootstrap.bind(udpProperties.getPort()).sync();
            channel = future.channel();
            running = true;
            log.info("udp服务器启动成功，监听端口: {}", udpProperties.getPort());
        } catch (InterruptedException e) {
            log.error("udp服务器启动被中断", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("udp服务器启动失败", e);
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }

    public synchronized void stopServer() {
        if (!running) {
            log.info("udp服务已停止");
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
            log.info("udp服务器已关闭");
        } catch (InterruptedException e) {
            log.error("udp服务器停止被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    public static class EvaluateServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            ByteBuf buf = msg.content();
            log.info("评估收到【{}】数据：{}", msg.sender(), ByteBufUtil.hexDump(buf.copy()));
        }
    }

}