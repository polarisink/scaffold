package github.polarisink.third.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author hzsk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    //private final RocketMQTemplate rocketMQTemplate;


    /**
     * @Author: liukang
     * @Date: 10:14 2021/5/4
     * @Parms [phone, content]
     * @ReturnType: void
     * @Description: 发送短信
     */
    @Override
    public void send(String phone, String content) {
        LOG.info("[发送短信]phone:{},content:{}", phone, content);

        // 封装成短信发送任务，放入MQ
        // 同步发送，消息完全被消费完才可以继续发送
        // rocketMQTemplate.syncSend(TOPIC_SMS_SEND, new SmsTask(phone, content));
        // convertAndSend:使用此方法，交换机会马上把所有的消息都交给所有消费者，消费者在自行处理，不会因为消费者处理慢而阻塞线程
        //rocketMQTemplate.convertAndSend(TOPIC_SMS_SEND, new SmsTask(phone, content));
        //直接发送不管消费者有没有接收到，丢失消息的可能性大,日志发送可以选这个
        //rocketMQTemplate.sendOneWay(TOPIC_SMS_SEND, new SmsTask(phone, content));
    }
}

