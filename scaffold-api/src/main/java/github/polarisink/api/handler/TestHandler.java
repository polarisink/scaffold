package github.polarisink.api.handler;

import github.polarisink.api.service.TestService;
import github.polarisink.dao.entity.primary.Archives;
import github.polarisink.third.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aries
 * @date 2022/8/17
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestHandler {

  private final TestService testService;
  private final SmsService smsService;

  @GetMapping("/get/{id}")
  public Archives getById(@PathVariable Long id) {
    return testService.getById(id);
  }

  @GetMapping("/add")
  public void addArchive() {
    testService.addArchive(new Archives().setMachNum("oiujns").setHandle("csujdasjna"));
  }


  @GetMapping("/pay/{money}")
  public String pay(@PathVariable Double money) {
    LOG.info("[支付]money:{}", money);
    //短信提醒支付成功
    smsService.send("18888888888", "你在xx商城成功支付" + money + "元");
    return "SUCCESS";
  }
}
