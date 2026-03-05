package com.scaffold.spi;

import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.ServiceLoader;

public class LoadServiceDemo {
    
    // 定义服务接口
    public interface MessageService {
        void sendMessage(String message);
    }
    
    // 实现类1
    public static class EmailService implements MessageService {
        @Override
        public void sendMessage(String message) {
            System.out.println("邮件发送：" + message);
        }
    }
    
    // 实现类2
    public static class SmsService implements MessageService {
        @Override
        public void sendMessage(String message) {
            System.out.println("短信发送：" + message);
        }
    }
    
    public static void main(String[] args) {
        // 加载服务
        ServiceLoader<MessageService> loader = ServiceLoaderUtil.load(MessageService.class);
        
        System.out.println("加载所有MessageService实现：");
        for (MessageService service : loader) {
            service.sendMessage("测试消息");
        }
        
        System.out.println("\n使用自定义类加载器：");
        ClassLoader customLoader = Thread.currentThread().getContextClassLoader();
        ServiceLoader<MessageService> loader2 = ServiceLoaderUtil.load(MessageService.class, customLoader);
        
        int count = 0;
        for (MessageService service : loader2) {
            count++;
            System.out.println("实现类" + count + "：" + service.getClass().getSimpleName());
        }
    }
}