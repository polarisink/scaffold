package github.polarisink.api.utils;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static github.polarisink.common.constant.AuthConst.SECRET;
import static github.polarisink.common.constant.AuthConst.TOKEN_PREFIX;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import github.polarisink.common.exception.AuthException;
import github.polarisink.common.utils.TimeUtils;
import github.polarisink.dao.bean.auth.Authentication;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


/**
 * Auth Util
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-24
 */
@Slf4j
public class AuthUtil {

  public static String generateToken(String uid, Long role, LocalDateTime expiration) {
    String credentials = getCredentials(uid, role);
    return JWT.create().withSubject(credentials).withExpiresAt(TimeUtils.localDateTime2date(expiration))
        .sign(HMAC512(SECRET.getBytes()));
  }

  private static String getCredentials(String uid, Long role) {
    return StrUtil.format("{},{}", uid, role);
  }

  private static Authentication parseCredentials(String credentials) {
    List<String> list = StrUtil.split(credentials, StrUtil.COMMA);
    String uid = list.get(0).trim();
    String roleStr = list.get(1).trim();
    Authentication authentication = new Authentication().setUid(Long.valueOf(uid));
    //新添加用户没有角色
    authentication.setRoleId(Long.valueOf(roleStr));
    return authentication;
  }

  public static Authentication getAuthorization(String token) {
    DecodedJWT decodedJWT;
    try {
      decodedJWT = JWT.require(HMAC512(SECRET.getBytes())).build().verify(token.replace(TOKEN_PREFIX, StrUtil.EMPTY));
    } catch (Throwable e) {
      LOG.warn("[AUTH]", e);
      throw new AuthException("登陆失败");
    }
    String subject = decodedJWT.getSubject();
    return parseCredentials(subject);
  }

}
