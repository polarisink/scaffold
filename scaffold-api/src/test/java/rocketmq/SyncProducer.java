package rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import static rocketmq.Common.ROCKETMQ_URL;

/*@SpringBootTest(classes = ScaffoldApplication.class)
@RunWith(SpringRunner.class)*/
public class SyncProducer {




  @Test
  public void createTopic() throws Exception {
    String group = "GID_TEST";
    // 初始化一个producer并设置Producer group name
    DefaultMQProducer producer = new DefaultMQProducer(group, false); //（1）
    // 设置NameServer地址
    producer.setNamesrvAddr(ROCKETMQ_URL);  //（2）
    // 启动producer
    producer.start();
    //producer.createTopic("TBW102",group,8);
    for (int i = 0; i < 100; i++) {
      // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
      Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));   //（3）
      // 利用producer进行发送，并同步等待发送结果
      SendResult sendResult = producer.send(msg, 5000);   //（4）
      System.out.printf("%s%n", sendResult);
    }
    // 一旦producer不再使用，关闭producer
    producer.shutdown();
  }
}