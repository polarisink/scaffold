package com.scaffold.spi;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.List;

public class LoggingFacadeSystem {
    
    // 日志级别
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    // 日志适配器接口
    public interface LoggerAdapter {
        String getName();
        int getPriority();
        void log(LogLevel level, String message);
        boolean isAvailable();
    }
    
    // Logback适配器
    public static class LogbackAdapter implements LoggerAdapter {
        @Override
        public String getName() {
            return "Logback";
        }
        
        @Override
        public int getPriority() {
            return 100;
        }
        
        @Override
        public void log(LogLevel level, String message) {
            String time = DateUtil.now();
            System.out.println("[Logback] " + time + " [" + level + "] " + message);
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
    
    // Log4j2适配器
    public static class Log4j2Adapter implements LoggerAdapter {
        @Override
        public String getName() {
            return "Log4j2";
        }
        
        @Override
        public int getPriority() {
            return 90;
        }
        
        @Override
        public void log(LogLevel level, String message) {
            String time = DateUtil.now();
            System.out.println("[Log4j2] " + time + " [" + level + "] " + message);
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
    
    // JDK Logging适配器
    public static class JdkLoggingAdapter implements LoggerAdapter {
        @Override
        public String getName() {
            return "JDK Logging";
        }
        
        @Override
        public int getPriority() {
            return 50;
        }
        
        @Override
        public void log(LogLevel level, String message) {
            String time = DateUtil.now();
            System.out.println("[JDK] " + time + " [" + level + "] " + message);
        }
        
        @Override
        public boolean isAvailable() {
            return true;
        }
    }
    
    // 日志门面
    static class Logger {
        private static LoggerAdapter adapter;
        
        static {
            initAdapter();
        }
        
        private static void initAdapter() {
            System.out.println("初始化日志适配器...");
            
            // 加载所有适配器
            List<LoggerAdapter> adapters = ServiceLoaderUtil.loadList(LoggerAdapter.class);
            
            if (adapters.isEmpty()) {
                System.out.println("未找到日志适配器，使用默认实现");
                adapter = new JdkLoggingAdapter();
                return;
            }
            
            // 按优先级排序，选择最高优先级的可用适配器
            adapter = adapters.stream()
                .filter(LoggerAdapter::isAvailable)
                .max((a1, a2) -> Integer.compare(a1.getPriority(), a2.getPriority()))
                .orElse(new JdkLoggingAdapter());
            
            System.out.println("已选择日志适配器：" + adapter.getName() + 
                             " (优先级：" + adapter.getPriority() + ")\n");
        }
        
        public static void debug(String message) {
            adapter.log(LogLevel.DEBUG, message);
        }
        
        public static void info(String message) {
            adapter.log(LogLevel.INFO, message);
        }
        
        public static void warn(String message) {
            adapter.log(LogLevel.WARN, message);
        }
        
        public static void error(String message) {
            adapter.log(LogLevel.ERROR, message);
        }
        
        public static void listAvailableAdapters() {
            List<LoggerAdapter> adapters = ServiceLoaderUtil.loadList(LoggerAdapter.class);
            System.out.println("可用的日志适配器：");
            for (LoggerAdapter a : adapters) {
                System.out.println("  - " + a.getName() + " (优先级：" + 
                                 a.getPriority() + ", 可用：" + a.isAvailable() + ")");
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("========== 日志门面系统 ==========\n");
        
        // 显示可用适配器
        Logger.listAvailableAdapters();
        
        System.out.println("\n开始记录日志：\n");
        
        // 使用日志门面
        Logger.debug("这是一条调试日志");
        Logger.info("应用程序已启动");
        Logger.warn("内存使用率较高");
        Logger.error("数据库连接失败");
        
        System.out.println("\n日志门面自动选择了优先级最高的适配器");
    }
}