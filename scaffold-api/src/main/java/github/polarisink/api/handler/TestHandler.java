package github.polarisink.api.handler;

import github.polarisink.api.service.TestService;
import github.polarisink.dao.entity.primary.Archives;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aries
 * @date 2022/8/17
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestHandler {
  private final TestService testService;
  @GetMapping("/get/{id}")
  public Archives getById(@PathVariable Long id){
    return testService.getById(id);
  }
}
