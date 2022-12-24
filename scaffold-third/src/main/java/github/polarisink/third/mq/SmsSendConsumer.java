package github.polarisink.third.mq;

import static github.polarisink.third.consts.RockerMqConst.TOPIC_SMS_SEND;

import github.polarisink.third.bean.SmsTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author aries
 * @date 2022/9/29
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_SMS_SEND, consumerGroup = "sms-group")
public class SmsSendConsumer implements RocketMQListener<SmsTask> {

  @Override
  public void onMessage(SmsTask smsTask) {
    LOG.info("[短信发送消费者]received message:{}", smsTask);
    LOG.info("[短信发送消费者]模拟发送，发送中......");
    LOG.info("[短信发送消费者]发送成功");
  }
}
