package github.polarisink.third.service;

/**
 * 短信发送服务
 *
 * @author aries
 * @date 2022/9/29
 */
public interface SmsService {

    /**
     * @param phone   手机号
     * @param content 短信内容
     */
    void send(String phone, String content);
}
