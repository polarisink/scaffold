package github.polarisink.api.handler;


import github.polarisink.mqtt.config.MqttGateway;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lqs
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/mqtt")
public class MqttHandler {

  @Resource
  private final MqttGateway mqttGateway;
  private final MqttPahoMessageDrivenChannelAdapter mqttSubscriber;

  /**
   * sendData 消息
   **/
  @RequestMapping("/sendMqtt1")
  public void sendMqtt(String sendData, String topic) {
    mqttGateway.sendToMqtt(topic, sendData);
  }

  /**
   * sendData 消息
   * qos 消息级别 （对应QOS0、QOS1，QOS2）
   * topic 订阅主题
   **/
  @RequestMapping("/sendMqtt2")
  public void sendMqtt(String sendData, int qos, String topic) {
    mqttGateway.sendToMqtt(topic, qos, sendData);
  }

  /**
   * 新增topic
   * @param topic
   */
  @GetMapping("/addTopic")
  public void addTopic(String topic) {
    mqttSubscriber.addTopic(topic, 0);
  }
}