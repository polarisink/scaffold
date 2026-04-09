package com.scaffold.log;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.base.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpMethod;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Aspect
public class LogAspect {

    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};
    private static final ThreadLocal<StopWatch> KEY_CACHE = new ThreadLocal<>();
    private static final int TWO_K = 2000;

    @SuppressWarnings("rawtypes")
    public static boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

    @Before(value = "@annotation(controllerLog)")
    public void doBefore(Log controllerLog) {
        StopWatch stopWatch = new StopWatch();
        KEY_CACHE.set(stopWatch);
        stopWatch.start();
    }

    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            OperateLogEvent event = new OperateLogEvent();
            HttpServletRequest request = ServletUtils.getRequest();
            event.setStatus(BusinessStatus.SUCCESS.ordinal());
            event.setIp(ServletUtils.getClientIP(request));
            event.setUrl(StrUtil.sub(request.getRequestURI(), 0, 255));
            event.setRequestMethod(request.getMethod());
            if (e != null) {
                event.setStatus(BusinessStatus.FAIL.ordinal());
                event.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 512));
            }
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            event.setMethod(className + "." + methodName + "()");
            StopWatch stopWatch = KEY_CACHE.get();
            stopWatch.stop();
            event.setCostTime((int) stopWatch.getTotalTimeMillis());
            getControllerMethodDescription(joinPoint, controllerLog, event, jsonResult);
            SpringUtil.getApplicationContext().publishEvent(event);
        } catch (Exception exp) {
            log.error("异常信息:{}", exp.getMessage());
        } finally {
            KEY_CACHE.remove();
        }
    }

    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperateLogEvent event, Object jsonResult) throws Exception {
        event.setBusinessType(log.businessType().ordinal());
        event.setTitle(log.title());
        if (log.isSaveRequestData()) {
            setRequestValue(joinPoint, event, log.excludeParamNames());
        }
        if (log.isSaveResponseData() && ObjectUtil.isNotNull(jsonResult)) {
            String json = JsonUtil.toJson(jsonResult);
            if (json.length() <= TWO_K) {
                event.setResult(json);
            }
        }
    }

    private void setRequestValue(JoinPoint joinPoint, OperateLogEvent event, String[] excludeParamNames) {
        Map<String, String> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String requestMethod = event.getRequestMethod();
        String param;
        if (MapUtil.isEmpty(paramsMap) && HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            param = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
        } else {
            MapUtil.removeAny(paramsMap, EXCLUDE_PROPERTIES);
            MapUtil.removeAny(paramsMap, excludeParamNames);
            param = JsonUtil.toJson(paramsMap);
        }
        if (param.length() <= TWO_K) {
            event.setParam(param);
        }
    }

    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringJoiner params = new StringJoiner(" ");
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                String str = JsonUtil.toJson(o);
                Dict dict = JsonUtil.convert(o, new TypeReference<>() {
                });
                if (MapUtil.isNotEmpty(dict)) {
                    MapUtil.removeAny(dict, EXCLUDE_PROPERTIES);
                    MapUtil.removeAny(dict, excludeParamNames);
                    str = JsonUtil.toJson(dict);
                }
                params.add(str);
            }
        }
        return params.toString();
    }
}
