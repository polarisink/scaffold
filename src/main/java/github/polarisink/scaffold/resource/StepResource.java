package github.polarisink.scaffold.resource;

import github.polarisink.scaffold.application.StepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lqs
 * @date 2023/6/29
 */
@RequestMapping("/step")
public class StepResource {

  private final StepService service;

  public StepResource(StepService stepService) {
    this.service = stepService;
  }

  @GetMapping("/ok")
  public String ok() {
    return "ok";
  }
}
