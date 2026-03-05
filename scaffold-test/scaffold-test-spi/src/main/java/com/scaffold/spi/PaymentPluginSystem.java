package com.scaffold.spi;

import cn.hutool.core.util.ServiceLoaderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPluginSystem {
    
    // 支付服务接口
    public interface PaymentService {
        String getPaymentType();
        boolean pay(String orderId, double amount);
        boolean refund(String orderId, double amount);
    }
    
    // 支付宝实现
    public static class AlipayService implements PaymentService {
        @Override
        public String getPaymentType() {
            return "alipay";
        }
        
        @Override
        public boolean pay(String orderId, double amount) {
            System.out.println("支付宝支付：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
        
        @Override
        public boolean refund(String orderId, double amount) {
            System.out.println("支付宝退款：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
    }
    
    // 微信支付实现
    public static class WechatPayService implements PaymentService {
        @Override
        public String getPaymentType() {
            return "wechat";
        }
        
        @Override
        public boolean pay(String orderId, double amount) {
            System.out.println("微信支付：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
        
        @Override
        public boolean refund(String orderId, double amount) {
            System.out.println("微信退款：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
    }
    
    // 银联支付实现
    public static class UnionPayService implements PaymentService {
        @Override
        public String getPaymentType() {
            return "unionpay";
        }
        
        @Override
        public boolean pay(String orderId, double amount) {
            System.out.println("银联支付：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
        
        @Override
        public boolean refund(String orderId, double amount) {
            System.out.println("银联退款：订单" + orderId + "，金额" + amount + "元");
            return true;
        }
    }
    
    // 支付管理器
    static class PaymentManager {
        private Map<String, PaymentService> paymentServices;
        
        public PaymentManager() {
            this.paymentServices = new HashMap<>();
            loadPaymentPlugins();
        }
        
        private void loadPaymentPlugins() {
            List<PaymentService> services = ServiceLoaderUtil.loadList(PaymentService.class);
            
            System.out.println("正在加载支付插件...");
            for (PaymentService service : services) {
                paymentServices.put(service.getPaymentType(), service);
                System.out.println("  已加载：" + service.getPaymentType() + " - " + 
                                 service.getClass().getSimpleName());
            }
            System.out.println("共加载 " + services.size() + " 个支付插件\n");
        }
        
        public boolean pay(String paymentType, String orderId, double amount) {
            PaymentService service = paymentServices.get(paymentType);
            if (service == null) {
                System.err.println("不支持的支付方式：" + paymentType);
                return false;
            }
            return service.pay(orderId, amount);
        }
        
        public boolean refund(String paymentType, String orderId, double amount) {
            PaymentService service = paymentServices.get(paymentType);
            if (service == null) {
                System.err.println("不支持的支付方式：" + paymentType);
                return false;
            }
            return service.refund(orderId, amount);
        }
        
        public void listAvailablePayments() {
            System.out.println("可用的支付方式：");
            paymentServices.keySet().forEach(type -> 
                System.out.println("  - " + type));
        }
    }
    
    public static void main(String[] args) {
        System.out.println("========== 插件化支付系统 ==========\n");
        
        PaymentManager manager = new PaymentManager();
        
        // 显示可用支付方式
        manager.listAvailablePayments();
        
        System.out.println("\n场景1：用户使用支付宝支付");
        manager.pay("alipay", "ORD20231201001", 299.00);
        
        System.out.println("\n场景2：用户使用微信支付");
        manager.pay("wechat", "ORD20231201002", 199.00);
        
        System.out.println("\n场景3：用户申请退款");
        manager.refund("alipay", "ORD20231201001", 299.00);
        
        System.out.println("\n场景4：使用不支持的支付方式");
        manager.pay("paypal", "ORD20231201003", 399.00);
    }
}