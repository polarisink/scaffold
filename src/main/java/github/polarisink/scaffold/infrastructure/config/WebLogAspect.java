package github.polarisink.scaffold.infrastructure.config;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author lqs http请求日志打印
 * @date 2021/11/6
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private static final ObjectMapper mapper = SpringUtil.getBean(ObjectMapper.class);

    /**
     * 以resource包下定义的所有请求为切入点
     */
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void webLog() {
    }


    /**
     * 环绕
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        //执行逻辑，计算执行时间
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        long stop = System.currentTimeMillis();
        //TODO 怎么合理的打印不同信息
        String argsStr = "can not be printed";
        String resultStr = "";
        try {
            resultStr = mapper.writeValueAsString(result);
        } catch (JsonProcessingException ignored) {
        }
        try {
            argsStr = mapper.writeValueAsString(point.getArgs());
        } catch (JsonProcessingException ignored) {
        }
        /*WebLog webLog = new WebLog();
        webLog.setUrl(request.getRequestURL().toString());
        webLog.setHttpMethod(request.getMethod());
        webLog.setClassMethod(point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
        webLog.setIp(request.getRemoteAddr());
        webLog.setTimeConsume(stop - start);
        webLog.setRequestArgs(argsStr);
        webLog.setResponseArgs(resultStr);*/
        LOG.info("\n*********************** Start ***********************\n" +
                        "* Request URL    : {}\n" +
                        "* HTTP Method    : {}\n" +
                        "* Class Method   : {}.{}\n" +
                        "* IP Address     : {}\n" +
                        "* Request Args   : {}\n" +
                        "* Time-Consuming : {}ms\n" +
                        "* Response Args  : {}\n" +
                        "* *********************** End ***********************\n",
                request.getRequestURL(), request.getMethod(), point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), request.getRemoteAddr(), argsStr, stop - start, resultStr);
        return result;
    }


    /*@Getter
    @Setter
    public static class WebLog {
        private String url;
        private String httpMethod;
        private String classMethod;
        private String ip;
        private String requestArgs;
        private String responseArgs;
        private Long timeConsume;

        @Override
        public String toString() {
            return String.format(
                    "\n*********************** Start ***********************\n" +
                            "* Request URL    : %s\n" +
                            "* HTTP Method    : %s\n" +
                            "* Class Method   : %s\n" +
                            "* IP Address     : %s\n" +
                            "* Request Args   : %s\n" +
                            "* Time-Consuming : %dms\n" +
                            "* Response Args  : %s\n" +
                            "* *********************** End ***********************\n", url, httpMethod, classMethod, ip, requestArgs, timeConsume, responseArgs);
        }
    }*/
}
