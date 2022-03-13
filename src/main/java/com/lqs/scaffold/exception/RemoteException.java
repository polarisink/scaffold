package com.lqs.scaffold.exception;

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
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author lqs
 * @date 2022/2/23
 */
public class RemoteException extends BaseException {


    public RemoteException(int code, String msg) {
        super(code, msg);
    }

    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(String format, Object... args) {
        super(format, args);
    }

	/**
	 * 打印restTemplate远程请求配置
	 *
	 * @author aries
	 * @date 2022/02/17
	 */
	public static class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

			private static final Logger log = LoggerFactory.getLogger(LogClientHttpRequestInterceptor.class);

			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte  [] body, ClientHttpRequestExecution execution) throws IOException {

					StopWatch stopWatch = new StopWatch();
					stopWatch.start();
					ClientHttpResponse response = execution.execute(request, body);
					stopWatch.stop();
					StringBuilder resBody = new StringBuilder();
					try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(),
									StandardCharsets.UTF_8))) {
							String line = bufferedReader.readLine();
							while (line != null) {
									resBody.append(line);
									line = bufferedReader.readLine();
							}
					}
					//当然图片、文件一类的就可以省了，打出日志没啥用处，此处的业务逻辑随意撸了，比如header头信息类似于  Accept 、Accept-Encoding 、Accept-Language、Connection 等等
					if (request.getHeaders().getContentType() != null && request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
							body = new byte[]{};
					}
					log.info("Request URL    : {}", request.getURI());
					log.info("HTTP Method    : {}", request.getMethodValue());
					log.info("Cost-Time      : {}", stopWatch.getLastTaskTimeMillis() + "ms");
					log.info("Status         : {}", response.getRawStatusCode());
					log.info("Body           : {}", new String(body, StandardCharsets.UTF_8));
					return response;
			}
	}

	/**
	 * @author lqs
	 * @describe http请求日志打印
	 * @date 2021/11/6
	 */
	@Aspect
	@Component
	public static class WebLogAspect {

		private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
		/**
		 * 超过timeLimit发出警告
		 */
		private final long timeLimit = 1500;
		private final ObjectMapper mapper;

		public WebLogAspect(ObjectMapper mapper) {
			this.mapper = mapper;
		}

		/**
		 * 以 controller 包下定义的所有请求为切入点
		 */
		@Pointcut("execution(public * com.lqs.scaffold.controller..*.*(..))")
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
				logger.info("Request Args   : args can not be printed");
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
			try {
				logger.info("Response Args  : {}", mapper.writeValueAsString(result));
			} catch (JsonProcessingException e) {
				logger.info("Response       : result can not be printed");
			}
			// 打印出参
			// 执行耗时
			long consume = System.currentTimeMillis() - startTime;
			logger.info("Time-Consuming : {} ms", consume);
			if (consume > timeLimit) {
				logger.warn("API执行时间过长…………");
			}
			return result;
		}

	}
}
