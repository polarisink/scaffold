package github.polarisink.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lqs
 * @describe http请求日志打印
 * @date 2021/11/6
 */
@Aspect
@Component
public class WebLogAspect {

	private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
	private final ObjectMapper mapper;

	private String locate = "";

	public WebLogAspect(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * 以 controller 包下定义的所有请求为切入点
	 */
	@Pointcut("execution(public * com.lqs.*.controller..*.*(..))")
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
		HttpServletRequest request = attributes.getRequest();

		logger.info("========================================== Start ==========================================");
		logger.info("Request URL    : {}", request.getRequestURL().toString());
		logger.info("HTTP Method    : {}", request.getMethod());
		logger.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
		logger.info("IP Address     : {}", request.getRemoteAddr());
		try {
			logger.info("Request Args   : {}", mapper.writeValueAsString(joinPoint.getArgs()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在切点之后织入
	 */
	@After("webLog()")
	public void doAfter() {
		logger.info("=========================================== End ===========================================");
		// 每个请求之间空一行
		logger.info("");
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
		long startTime = System.currentTimeMillis();
		Object result = proceedingJoinPoint.proceed();
		// 打印出参
		try {
			logger.info("Response Args  : {}", mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		// 执行耗时
		logger.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
		return result;
	}

}
