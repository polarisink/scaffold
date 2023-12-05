package github.polarisink.scaffold.resource;

import github.polarisink.scaffold.infrastructure.util.TimeUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class RabbitmqResource {

    @Resource
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "Test message, hello world!";
        String createTime = TimeUtil.toDatetime(LocalDateTime.now());
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("directExchange", "directRouting", map);
        return "ok";
    }

    @GetMapping("/sendTopicFirstMessage")
    public String sendTopicFirstMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: first";
        String createTime = TimeUtil.toDatetime(LocalDateTime.now());
        Map<String, Object> firstMap = new HashMap<>();
        firstMap.put("messageId", messageId);
        firstMap.put("messageData", messageData);
        firstMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("topicExchange", "topic.first", firstMap);
        return "ok";
    }

    @GetMapping("/sendTopicSecondMessage")
    public String sendTopicSecondMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: second";

        String createTime = TimeUtil.toDatetime(LocalDateTime.now());
        Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("messageId", messageId);
        secondMap.put("messageData", messageData);
        secondMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("topicExchange", "topic.second", secondMap);
        return "ok";
    }

    @GetMapping("/sendFanoutMessage")
    public String sendFanoutMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: all";
        String createTime = TimeUtil.toDatetime(LocalDateTime.now());
        Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("messageId", messageId);
        secondMap.put("messageData", messageData);
        secondMap.put("createTime", createTime);
        rabbitTemplate.convertAndSend("fanoutExchange", "fanout.send", secondMap);
        return "ok";
    }
}