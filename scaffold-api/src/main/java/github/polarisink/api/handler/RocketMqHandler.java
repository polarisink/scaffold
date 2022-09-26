package github.polarisink.api.handler;

import github.polarisink.dao.entity.primary.Archives;
import github.polarisink.dao.repo.primary.ArchivesRepo;
import github.polarisink.mq.MqProducerService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hzsk
 */
@RestController
@RequestMapping("/rocketmq")
@RequiredArgsConstructor
public class RocketMqHandler {

  private final MqProducerService mqProducerService;
  private final ArchivesRepo archivesRepo;

  @GetMapping("/send")
  public void send() {
    Archives user = archivesRepo.findAll().get(0);
    mqProducerService.send(user);
  }

  @GetMapping("/sendTag")
  public SendResult sendTag() {
    return mqProducerService.sendTagMsg("带有tag的字符消息");
  }

}
