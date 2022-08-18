package github.polarisink.api.utils;

import cn.hutool.extra.servlet.ServletUtil;
import github.polarisink.common.asserts.ArgumentE;
import github.polarisink.dao.auth.Authentication;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;

import static cn.hutool.core.text.CharSequenceUtil.trimToNull;
import static github.polarisink.common.constant.AuthConst.AUTH_HEADER;

/**
 * 获取授权信息,可以代替AdminPrincipalResolver
 * AdminPrincipalResolver缺点:侵入到业务方法的签名，需要从controller传递到service
 *
 * @author aries
 * @date 2022/7/20
 */
public class ContentUtil {
  public static Authentication getSubject() {
    HttpServletRequest request = getRequest();
    String header = trimToNull(request.getHeader(AUTH_HEADER));
    ArgumentE.VALID_ERROR.assertNotEmptyWithMsg(header, "未登录");
    return AuthUtil.getAuthorization(header);
  }

  public static Authentication getSubject(HttpServletRequest request) {
    String header = trimToNull(request.getHeader(AUTH_HEADER));
    ArgumentE.VALID_ERROR.assertNotEmptyWithMsg(header, "未登录");
    return AuthUtil.getAuthorization(header);
  }

  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    return ((ServletRequestAttributes) requestAttributes).getRequest();
  }

  public static String getClientIp(HttpServletRequest request) {
    return ServletUtil.getClientIP(request);
  }

  public static String getClientIp() {
    return ServletUtil.getClientIP(getRequest());
  }

  public static String getUserAgent() {
    return getUserAgent(getRequest());
  }

  /**
   * @param request 请求
   * @return ua
   */
  public static String getUserAgent(HttpServletRequest request) {
    String ua = request.getHeader("User-Agent");
    return ua != null ? ua : "";
  }

}
