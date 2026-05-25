package com.scaffold.udp.server;

import cn.hutool.core.util.StrUtil;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.udp.IBytes;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;


/**
 * netty-udp服务器
 * 需要确保被其他class调用时已经初始化好，否则可能报错
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MasterServer implements SmartLifecycle {
    private final UdpProperties udpProperties;
    private volatile boolean running = false;
    private EventLoopGroup workerGroup;
    private Channel channel;

    private String getName() {
        return "master-udp";
    }

    public <T extends IBytes> void send(T t) {
        send(JsonUtil.toJson(t), udpProperties.getReceiverHost(), udpProperties.getPort(), t.toBuf(), t.getName(), t.enableLog());
    }

    public <T extends IBytes> void sendBatch(List<T> bufList) {
        if (bufList == null || bufList.isEmpty()) {
            return;
        }
        sendBatch(udpProperties.getReceiverHost(), udpProperties.getPort(), bufList, bufList.getFirst().enableLog());
    }

    /**
     * 批量发送杂波数据，此方法会锁定整个发送过程，确保杂波数据完整发送完毕后才能发送其他数据
     *
     * @param list 发送的数据
     */
    public <T extends IBytes> void sendBatch(String host, int port, List<T> list, boolean enableLog) {
        try {
            for (T t : list) {
                doSend(JsonUtil.toJson(t), host, port, t.toBuf(), t.getName(), enableLog);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void doSend(String json, String host, int port, ByteBuf buf, String logName, boolean enableLog) throws InterruptedException {
        String s = logName == null ? "" : logName;
        InetSocketAddress receiver = new InetSocketAddress(host, port);
        // InetSocketAddress sender = new InetSocketAddress(NetUtil.getLocalhostStr(), port);
        int length = buf.readableBytes();
        boolean needLogDetail = log.isDebugEnabled() && (enableLog || !running);
        String jsonText = needLogDetail ? StrUtil.blankToDefault(json, "-") : "";
        String hexText = needLogDetail ? UdpBindLogUtil.formatHex(ByteBufUtil.hexDump(buf, buf.readerIndex(), length)) : "";
        int sleepTime = 1;
        if (!running) {
            if (log.isDebugEnabled()) {
                log.debug(UdpBindLogUtil.formatSendLog(getName(), false, s, receiver, length, sleepTime, getName() + "服务器未启动", jsonText, hexText));
            }
            return;
        }
        DatagramPacket packet = new DatagramPacket(buf, receiver);
        ChannelFuture channelFuture = channel.writeAndFlush(packet);
        if (enableLog) {
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (!log.isDebugEnabled()) {
                    return;
                }
                if (future.isSuccess()) {
                    log.debug(UdpBindLogUtil.formatSendLog(getName(), true, s, receiver, length, sleepTime, null, jsonText, hexText));
                } else {
                    log.debug(UdpBindLogUtil.formatSendLog(getName(), false, s, receiver, length, sleepTime, future.cause().getMessage(), jsonText, hexText));
                }
            });
        }
    }

    public void send(String json, String host, int port, ByteBuf buf, String logName, boolean enableLog) {
        try {
            doSend(json, host, port, buf, logName, enableLog);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            log.info("{}服务已启动", getName());
            return;
        }
        String bindTarget = UdpBindLogUtil.bindTarget(udpProperties.getLocalhost(), udpProperties.getPort());
        try {
            // 业务线程池，处理业务逻辑
            // Netty工作线程组，处理网络IO
            workerGroup = new NioEventLoopGroup(4);
            Bootstrap bootstrap = new Bootstrap().group(workerGroup).channel(NioDatagramChannel.class)// 设置通道类型为UDP
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 5)// 设置接收缓冲区大小
                    .option(ChannelOption.SO_BROADCAST, true)// 开启广播
                    .handler(new ChannelInitializer<NioDatagramChannel>() {// 设置ChannelInitializer
                        @Override
                        protected void initChannel(NioDatagramChannel ch) {
                            ch.pipeline().addLast(new MasterServerHandler());
                        }
                    });
            ChannelFuture future = StrUtil.isBlank(udpProperties.getLocalhost())
                    // 不指定ip
                    ? bootstrap.bind(udpProperties.getPort()).sync()
                    // 指定ip
                    : bootstrap.bind(udpProperties.getLocalhost(), udpProperties.getPort()).sync();
            // ChannelFuture future = bootstrap.bind(udpProperties.getPort()).sync();
            channel = future.channel();
            running = true;
            log.info("{}服务器启动成功，{}", getName(), bindTarget);
        } catch (InterruptedException e) {
            log.error("{}服务器启动被中断，{}", getName(), bindTarget, e);
            running = false;
            channel = null;
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                workerGroup = null;
            }
            Thread.currentThread().interrupt();
            throw new IllegalStateException(getName() + "服务器启动被中断，" + bindTarget, e);
        } catch (Exception e) {
            log.error("{}}服务器启动失败，{}", getName(), bindTarget, e);
            running = false;
            channel = null;
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                workerGroup = null;
            }
            throw new IllegalStateException(getName() + "服务器启动失败，" + bindTarget, e);
        }
    }

    public synchronized void stopServer() {
        if (!running) {
            log.info("{}服务已停止", getName());
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
            log.info("{}}服务器已关闭", getName());
        } catch (InterruptedException e) {
            log.error("{}}服务器停止被中断", getName(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        startServer();
    }

    @Override
    public void stop() {
        stopServer();
    }

    @Override
    public void stop(Runnable callback) {
        try {
            stopServer();
        } finally {
            callback.run();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return 150;
    }

    public class MasterServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        /**
         * 日志打印方法
         *
         * @param sender 发送者
         * @param type   数据类型
         * @param buf    数据buffer
         * @param obj    转json的结果
         */
        private void printLog(InetSocketAddress sender, String type, ByteBuf buf, Object obj) {
            try {
                if (buf.readableBytes() < 200) {
                    log.debug("收到来自{}的{},原始数据长度为：{},数据为{},结果为{}", sender, type, buf.readableBytes(), ByteBufUtil.hexDump(buf), JsonUtil.toJson(obj));
                }
            } finally {
                ReferenceCountUtil.release(buf);
            }
        }

        /**
         *
         * @param ctx
         * @param packet
         */
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
            ByteBuf buf = packet.content();
            if (buf.readableBytes() <= 4) {// 反馈数据不需要处理
                return;
            }
            InetSocketAddress sender = packet.sender();
            printLog(sender, "原始数据", buf.copy(), "");
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("{}}处理异常", getName(), cause);
        }

    }

}
