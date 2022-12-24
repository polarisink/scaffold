package rocketmq;

import static rocketmq.Common.ROCKETMQ_URL;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

public class Consumer {

  public static void main(String[] args) throws MQClientException {
    // 初始化consumer，并设置consumer group name
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
    // 设置NameServer地址 
    consumer.setNamesrvAddr(ROCKETMQ_URL);
    //订阅一个或多个topic，并指定tag过滤条件，这里指定*表示接收所有tag的消息
    consumer.subscribe("TopicTest", "*");
    //注册回调接口来处理从Broker中收到的消息
    consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
      System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
      // 返回消息消费状态，ConsumeConcurrentlyStatus.CONSUME_SUCCESS为消费成功
      return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    });
    // 启动Consumer
    consumer.start();
    System.out.printf("Consumer Started.%n");
  }
}