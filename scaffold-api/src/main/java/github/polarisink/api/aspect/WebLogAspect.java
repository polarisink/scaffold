package github.polarisink.api.aspect;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author lqs
 * http请求日志打印
 * @date 2021/11/6
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WebLogAspect {
  private static final long LENGTH = 10000;
  private final ObjectMapper mapper;

  @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) && @annotation(org.springframework.web.bind.annotation.PostMapping) " +
      "&& @annotation(org.springframework.web.bind.annotation.PutMapping) && @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
      "&& @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(org.springframework.web.bind.annotation.PatchMapping)")
  public void webLog() {
  }

  /**
   * 在切点之前织入
   *
   * @param joinPoint
   * @throws Throwable
   */
  @Before("webLog()")
  public void doBefore(JoinPoint joinPoint) {
    // 开始打印请求日志
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
    LOG.info("========================================== Start ==========================================");
    LOG.info("Request URL    : {}", request.getRequestURL().toString());
    LOG.info("HTTP Method    : {}", request.getMethod());
    LOG.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    LOG.info("IP Address     : {}", request.getRemoteAddr());

    String argsStr;
    try {
      /* todo 怎么保护敏感信息
       * 1、使用@NoLOG注解避免打印日志，精确到接口级别
       * 2、简单的维护敏感字段即可
       */
      argsStr = mapper.writeValueAsString(joinPoint.getArgs());
    } catch (JsonProcessingException e) {
      LOG.info("Request Args   : can not be printed");
      return;
    }
    if (isPrint(argsStr)) {
      LOG.info("Response Args  : {}", argsStr);
    } else {
      LOG.info("Request Args   : can not be printed");
    }
  }

  /**
   * TODO 找更优雅的实现方式
   *
   * @param argsStr
   * @return
   */
  private boolean isPrint(String argsStr) {
    if (argsStr.length() > LENGTH) {
      return false;
    }
    if (StrUtil.containsAny(argsStr, "password", "token")) {
      return false;
    }
    return true;
  }


  /**
   * 在切点之后织入
   */
  @After("webLog()")
  public void doAfter() {
    LOG.info("=========================================== End ===========================================");
    // 每个请求之间空一行
    LOG.info(StrUtil.EMPTY);
  }

  /**
   * 环绕
   *
   * @param proceedingJoinPoint
   * @return
   * @throws Throwable
   */
  @Around("webLog()")
  public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result = proceedingJoinPoint.proceed();
    String resultStr;
    try {
      resultStr = mapper.writeValueAsString(result);
    } catch (JsonProcessingException e) {
      LOG.info("Response Args  : can not be printed");
      return result;
    }
    if (resultStr.length() > LENGTH) {
      LOG.info("Response Args  : can not be printed");
    } else {
      LOG.info("Response Args  : {}", resultStr);
    }
    // 打印出参
    // 执行耗时
    stopWatch.stop();
    long consume = stopWatch.getLastTaskTimeMillis();
    LOG.info("Time-Consuming : {} ms", consume);
    /*
      超过timeLimit发出警告
     */
    long timeLimit = 1500;
    if (consume > timeLimit) {
      LOG.info("API执行时间过长......");
    }
    return result;
  }

}
