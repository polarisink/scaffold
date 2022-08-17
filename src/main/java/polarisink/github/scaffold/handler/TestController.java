package polarisink.github.scaffold.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import polarisink.github.scaffold.bean.request.ArchivesAddRequest;
import polarisink.github.scaffold.entity.mysql.primary.Archives;
import polarisink.github.scaffold.service.TestService;

import javax.validation.Valid;

/**
 * @author aries
 * @date 2022/8/17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
  private final TestService testService;

  @PostMapping("/add")
  public Archives add(@Valid ArchivesAddRequest request) {
    return testService.add(request);
  }
}
