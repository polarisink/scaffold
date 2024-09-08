package com.scaffold.core.log.aspect;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scaffold.core.base.util.JsonUtil;
import com.scaffold.core.base.util.ServletUtils;
import com.scaffold.core.log.annotation.Log;
import com.scaffold.core.log.event.OperateLogEvent;
import com.scaffold.core.log.vo.BusinessStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 操作日志记录处理
 *
 * @author Lion Li
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};


    /**
     * 计时 key
     */
    private static final ThreadLocal<StopWatch> KEY_CACHE = new ThreadLocal<>();

    private static final int TWO_K = 2000;

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
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

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(Log controllerLog) {
        StopWatch stopWatch = new StopWatch();
        KEY_CACHE.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            OperateLogEvent event = new OperateLogEvent();
            //跟网络请求相关的参数
            HttpServletRequest request = ServletUtils.getRequest();
            event.setStatus(BusinessStatus.SUCCESS.ordinal());
            event.setIp(ServletUtils.getClientIP(request));
            event.setUrl(StrUtil.sub(request.getRequestURI(), 0, 255));
            event.setRequestMethod(request.getMethod());
            //异常相关
            if (e != null) {
                event.setStatus(BusinessStatus.FAIL.ordinal());
                event.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 512));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            event.setMethod(className + "." + methodName + "()");
            // 设置消耗时间
            StopWatch stopWatch = KEY_CACHE.get();
            stopWatch.stop();
            event.setCostTime((int) stopWatch.getTotalTimeMillis());
            // 发布事件保存数据库
            getControllerMethodDescription(joinPoint, controllerLog, event, jsonResult);
            SpringUtil.getApplicationContext().publishEvent(event);
            //todo 打印日志
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage());
        } finally {
            KEY_CACHE.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log   日志
     * @param event 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperateLogEvent event, Object jsonResult) throws Exception {
        // 设置action动作
        event.setBusinessType(log.businessType().ordinal());
        // 设置标题
        event.setTitle(log.title());
        // 设置操作人类别
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, event, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && ObjectUtil.isNotNull(jsonResult)) {
            String json = JsonUtil.toJson(jsonResult);
            //超过2000就不存了，否则截取之后存，前端最后也不会显示
            if (json.length() <= TWO_K) {
                event.setResult(json);
            }
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param event 操作日志
     */
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
        //小于2000才设置，否则直接抛弃
        if (param.length() <= TWO_K) {
            event.setParam(param);
        }
    }

    /**
     * 参数拼装
     */
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
