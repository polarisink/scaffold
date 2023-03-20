package github.polarisink.api.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aries
 * @date 2022/9/29
 */
@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisHandler {

    @GetMapping("/cacheput/{id}")
    //@CachePut(cacheNames = "PUT#30s")
    public void cachePut(@PathVariable Long id) {
        LOG.info("缓存了id: {}", id);
    }

}
