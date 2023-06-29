package github.polarisink.scaffold.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简单的key/value键值对
 *
 * @author aries
 * @date 2022/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue {

    private Integer type;
    private String name;
}
