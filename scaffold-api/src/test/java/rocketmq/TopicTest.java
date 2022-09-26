package rocketmq;

import github.polarisink.ScaffoldApplication;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author aries
 * @date 2022/9/26
 */
@SpringBootTest(classes = ScaffoldApplication.class)
@RunWith(SpringRunner.class)
public class TopicTest {

  @Test
  public void test() throws MQClientException {
    DefaultMQProducer producer = new DefaultMQProducer("lqs");
    producer.setNamesrvAddr("1.13.169.163:9876");
    producer.start();
    producer.createTopic("broker_lqs_im", "my-topic", 8);
    producer.shutdown();
  }
}
