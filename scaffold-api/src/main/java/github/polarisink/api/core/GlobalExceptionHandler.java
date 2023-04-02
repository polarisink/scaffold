package github.polarisink.api.core;

import static github.polarisink.common.asserts.BaseE.BASE;
import static github.polarisink.common.asserts.BaseE.SERVER_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.polarisink.common.asserts.ArgumentE;
import github.polarisink.common.asserts.ServletE;
import github.polarisink.common.exception.ArgumentException;
import github.polarisink.common.exception.AuthException;
import github.polarisink.common.exception.BaseException;
import github.polarisink.common.exception.BusinessException;
import github.polarisink.dao.wrapper.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * <p>全局异常处理器</p>
 *
 * @author aries
 * @date 2022/5/2
 */
@Slf4j
@Component
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {

  /**
   * 生产环境
   */
  private final static String ENV_PROD = "prod";
  private final ObjectMapper mapper;

  /**
   * 当前环境
   */
  @Value("${spring.profiles.active}")
  private String profile;


  @Override
  public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    if (o == null) {
      return Response.of(null);
    }
    if (o instanceof Response) {
      return o;
    }
    return Response.of(o);
  }

  //==============================全局异常处理部分=============================

  /**
   * 业务异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = BusinessException.class)
  public Response<Void> handleBusinessException(BusinessException e) {
    LOG.error(e.getMessage(), e);
    return Response.fail(e.getResponseEnum().getCode(), e.getMessage());
  }

  /**
   * 鉴权异常
   *
   * @param e
   * @return
   */
  @ExceptionHandler(value = AuthException.class)
  public Response<Void> handleBusinessException(AuthException e) {
    LOG.error(e.getMessage(), e);
    return Response.fail(BASE.getCode(), e.getMessage());
  }

  /**
   * 自定义异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = BaseException.class)
  public Response<Void> handleBaseException(BaseException e) {
    LOG.error(e.getMessage(), e);
    return Response.fail(e.getResponseEnum().getCode(), e.getMessage());
  }

  /**
   * 自定义异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = ArgumentException.class)
  public Response<Void> handleArgException(ArgumentException e) {
    LOG.error(e.getMessage(), e);
    return Response.fail(e.getResponseEnum().getCode(), e.getMessage());
  }

  /**
   * 参数异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = IllegalArgumentException.class)
  public Response<Void> handleIllegalArgumentException(IllegalArgumentException e) {
    LOG.error(e.getMessage(), e);
    return Response.fail(SERVER_ERROR.getCode(), e.getMessage());
  }

  /**
   * Controller上一层相关异常
   *
   * @param e 异常
   * @return 异常结果
   */
  /*@formatter:off*/
  @ExceptionHandler({
      NoHandlerFoundException.class,
      HttpRequestMethodNotSupportedException.class,
      HttpMediaTypeNotSupportedException.class,
      HttpMediaTypeNotAcceptableException.class,
      MissingPathVariableException.class,
      MissingServletRequestParameterException.class,
      TypeMismatchException.class,
      HttpMessageNotReadableException.class,
      HttpMessageNotWritableException.class,
      // BindException.class,
      // MethodArgumentNotValidException.class
      ServletRequestBindingException.class,
      ConversionNotSupportedException.class,
      MissingServletRequestPartException.class,
      AsyncRequestTimeoutException.class})
  public Response<Void> handleServletException(Exception e) {
    /*@formatter:on*/
    LOG.error(e.getMessage(), e);
    int code = SERVER_ERROR.getCode();
    try {
      ServletE servletExceptionEnum = ServletE.valueOf(e.getClass().getSimpleName());
      code = servletExceptionEnum.getCode();
    } catch (IllegalArgumentException e1) {
      LOG.error("class [{}] not defined in enum {}", e.getClass().getName(), ServletE.class.getName());
    }
    if (ENV_PROD.equals(profile)) {
      // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
      code = SERVER_ERROR.getCode();
      BaseException baseException = new BaseException(SERVER_ERROR);
      String message = baseException.getMessage();
      return Response.of(code, message, null);
    }
    return Response.fail(code, e.getMessage());
  }


  /**
   * 参数绑定异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = BindException.class)
  public Response<Void> handleBindException(BindException e) {
    LOG.error("参数绑定异常", e);
    return wrapperBindingResult(e.getBindingResult());
  }

  /**
   * 参数校验(Valid)异常，将校验失败的所有异常组合成一条错误信息
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public Response<Void> handleValidException(MethodArgumentNotValidException e) {
    LOG.error("参数绑定校验异常", e);
    return wrapperBindingResult(e.getBindingResult());
  }

  /**
   * 未定义异常
   *
   * @param e 异常
   * @return 异常结果
   */
  @ExceptionHandler(value = Exception.class)
  public Response<Void> handleException(Exception e) {
    LOG.error(e.getMessage(), e);
    if (ENV_PROD.equals(profile)) {
      // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
      int code = SERVER_ERROR.getCode();
      BaseException baseException = new BaseException(SERVER_ERROR);
      String message = baseException.getMessage();
      return Response.of(code, message, null);
    }
    return Response.fail(SERVER_ERROR.getCode(), e.getMessage());
  }

  /**
   * 包装绑定异常结果
   *
   * @param bindingResult 绑定结果
   * @return 异常结果
   */
  private Response<Void> wrapperBindingResult(BindingResult bindingResult) {
    StringBuilder msg = new StringBuilder();
    for (ObjectError error : bindingResult.getAllErrors()) {
      msg.append(", ");
      if (error instanceof FieldError) {
        msg.append(((FieldError) error).getField()).append(": ");
      }
      msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
    }
    return Response.fail(ArgumentE.BASE.getCode(), msg.substring(2));
  }
}
