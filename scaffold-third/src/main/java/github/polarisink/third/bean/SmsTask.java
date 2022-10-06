package github.polarisink.third.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hzsk
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsTask {
    private String phone;
    private String content;
}

