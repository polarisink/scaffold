package github.polarisink.dao.dto;

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
public class Kv {
  private Integer key;
  private String value;
}
