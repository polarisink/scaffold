package com.scaffold.udp.server;

import cn.hutool.core.util.StrUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

final class UdpBindLogUtil {

    private UdpBindLogUtil() {
    }

    static String bindTarget(String host, int port) {
        if (StrUtil.isBlank(host)) {
            return "监听端口: " + port;
        }
        String nicName = resolveNetworkInterfaceName(host);
        if (StrUtil.isBlank(nicName)) {
            return "绑定地址: " + host + ", 监听端口: " + port;
        }
        return "绑定地址: " + host + " (网卡: " + nicName + "), 监听端口: " + port;
    }

    static String formatSendLog(String serverName, boolean success, String logName, InetSocketAddress receiver, int length, Integer sleepTime, String reason, String json, String hex) {
        String jsonText = StrUtil.blankToDefault(json, "-");
        String hexText = StrUtil.blankToDefault(hex, "-");
        StringBuilder builder = new StringBuilder(256 + jsonText.length() + hexText.length());
        builder.append("\n")
                .append("┌─ ").append(StrUtil.blankToDefault(serverName, "udp")).append(" 发送").append(success ? "成功" : "失败")
                .append(" | 报文名称：").append(StrUtil.blankToDefault(logName, "-"))
                .append(" | 目标地址：").append(receiver)
                .append(" | 报文长度：").append(length).append(" bytes");
        if (sleepTime != null) {
            builder.append(" | 发送间隔：").append(sleepTime).append(" ms");
        }
        if (StrUtil.isNotBlank(reason)) {
            builder.append("\n").append("├─ 失败原因：").append(reason);
        }
        if (StrUtil.isNotBlank(json)) {
            builder.append("\n").append("├─ json数据：").append(jsonText);
        }
        builder.append("\n").append("├─ hex数据：").append(hexText)
                .append("\n").append("└─ 结束");
        return builder.toString();
    }

    static String formatHex(String hex) {
        if (StrUtil.isBlank(hex)) {
            return "-";
        }
        StringBuilder builder = new StringBuilder(hex.length() + hex.length() / 2);
        int bytes = hex.length() / 2;
        for (int i = 0; i < bytes; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            int start = i * 2;
            builder.append(hex, start, start + 2);
        }
        return builder.toString();
    }

    private static String resolveNetworkInterfaceName(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            return networkInterface == null ? null : networkInterface.getName();
        } catch (Exception ignored) {
            return null;
        }
    }
}
