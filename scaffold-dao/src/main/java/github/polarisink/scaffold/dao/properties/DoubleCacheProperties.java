package github.polarisink.scaffold.dao.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-28 15:11
 **/
@Data
@Component
@ConfigurationProperties(prefix = "double-cache")
public class DoubleCacheProperties {
    private Boolean allowNull = true;
    private Integer init = 100;
    private Integer max = 1000;
    private Long expireAfterWrite ;
    private Long expireAfterAccess;
    private Long refreshAfterWrite;
    private Long redisExpire;
}
