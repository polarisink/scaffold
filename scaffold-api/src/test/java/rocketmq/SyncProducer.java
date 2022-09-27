package rocketmq;

import github.polarisink.ScaffoldApplication;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootTest(classes = ScaffoldApplication.class)
@RunWith(SpringRunner.class)
public class SyncProducer {
  @Test
  public void createTopic() throws Exception {
    String s = "E:\\ideaProjects\\scaffold\\.env";
    //Resource resource = new FileUrlResource(s);
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(s)) {
      props.load(in);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 通过key获取value
    // 使用了env进行配置,现在不知道如何解决
    String rocketmqUrl = props.getProperty("ROCKETMQ_URL");
    String group = "GID_TEST";
    // 初始化一个producer并设置Producer group name
    DefaultMQProducer producer = new DefaultMQProducer(group); //（1）
    // 设置NameServer地址
    producer.setNamesrvAddr(rocketmqUrl);  //（2）
    // 启动producer
    producer.start();
    producer.createTopic("TopicTest",group,8);
   /* for (int i = 0; i < 100; i++) {
      // 创建一条消息，并指定topic、tag、body等信息，tag可以理解成标签，对消息进行再归类，RocketMQ可以在消费端对tag进行过滤
      Message msg = new Message("TopicTest" *//* Topic *//*, "TagA" *//* Tag *//*, ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) *//* Message body *//*);   //（3）
      // 利用producer进行发送，并同步等待发送结果
      SendResult sendResult = producer.send(msg, 5000);   //（4）
      System.out.printf("%s%n", sendResult);
    }*/
    // 一旦producer不再使用，关闭producer
    producer.shutdown();
  }
}