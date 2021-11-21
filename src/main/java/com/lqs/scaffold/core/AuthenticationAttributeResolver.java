package com.lqs.scaffold.core;


import com.lqs.scaffold.annotations.NoSignIn;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Authentication Attribute Resolver
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-24
 */
@AllArgsConstructor
public class AuthenticationAttributeResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Method method = parameter.getMethod();
    NoSignIn noSignIn = method.getAnnotation(NoSignIn.class);
    if (noSignIn != null) {
      return false;
    }
    return parameter.getParameterType().equals(Authentication.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    return null;
  }

}
