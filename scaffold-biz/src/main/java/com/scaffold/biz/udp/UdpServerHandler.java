package com.scaffold.biz.udp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("UDP处理异常", cause);
    }


    private void processPacket(DatagramPacket packet) {
        ByteBuf buf = packet.content();
    }
}