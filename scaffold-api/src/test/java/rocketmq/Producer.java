package rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static rocketmq.Common.ROCKETMQ_URL;

public class Producer {
  public static void main(String[] args) throws UnsupportedEncodingException {
    try {
      DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
      producer.setNamesrvAddr(ROCKETMQ_URL);
      producer.start();
      String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
      for (int i = 0; i < 100; i++) {
        int orderId = i % 10;
        var msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i, ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
          @Override
          public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
            Integer id = (Integer) arg;
            int index = id % mqs.size();
            return mqs.get(index);
          }
        }, orderId);

        System.out.printf("%s%n", sendResult);
      }

      producer.shutdown();
    } catch (MQClientException | RemotingException | MQBrokerException |
             InterruptedException e) {
      e.printStackTrace();
    }
  }
}