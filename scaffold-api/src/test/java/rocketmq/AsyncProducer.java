package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import static rocketmq.Common.ROCKETMQ_URL;

public class AsyncProducer {

    public static void main(String[] args) throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name", false);
        // 设置NameServer地址
        producer.setNamesrvAddr(ROCKETMQ_URL);
        // 启动producer
        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.start();
        for (int i = 0; i < 100; i++) {
            final int index = i;
            // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
            Message msg = new Message("TopicTest", "TagA", "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 异步发送消息, 发送结果通过callback返回给客户端
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }
        //有回调,不能shutdown
        //producer.shutdown();
    }
}