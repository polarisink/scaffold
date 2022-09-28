package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

import static rocketmq.Common.ROCKETMQ_URL;

public class SimpleBatchProducer {

  public static void main(String[] args) throws Exception {
    DefaultMQProducer producer = new DefaultMQProducer("BatchProducerGroupName");
      producer.setNamesrvAddr(ROCKETMQ_URL);
      producer.start();
    //If you just send messages of no more than 1MiB at a time, it is easy to use batch
    //Messages of the same batch should have: same topic, same waitStoreMsgOK and no schedule support
    String topic = "BatchTest";
    List<Message> messages = new ArrayList<>();
    messages.add(new Message(topic, "Tag", "OrderID001", "Hello world 0".getBytes()));
    messages.add(new Message(topic, "Tag", "OrderID002", "Hello world 1".getBytes()));
    messages.add(new Message(topic, "Tag", "OrderID003", "Hello world 2".getBytes()));
    producer.send(messages);
  }
}