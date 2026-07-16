package com.scaffold.web.config;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.scaffold.base.exception.BaseException;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.base.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.concurrent.CompletionException;

import static com.scaffold.base.constant.GlobalConstant.GLOBAL_ERROR_CODE;

/**
 * 全局异常处理器
 *
 * @author miaol
 * @date 2020-04-11 10:14
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@ConditionalOnWebApplication
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {

    private final WebProperties webProperties;

    private final PathMatcher pathMatcher = new AntPathMatcher();


    /**
     * 处理自定义异常
     *
     * @param e 异常
     */
    @ExceptionHandler({BaseException.class})
    @ResponseBody
    public R<Void> handleException(BaseException e) {
        String msg = e.getMessage();
        if (CharSequenceUtil.isBlank(msg)) {
            msg = serverErrorMessage();
        }
        return R.failed(GLOBAL_ERROR_CODE, msg);
    }


    /**
     * 处理非法参数异常
     *
     * @param e e
     * @return {@link R}<{@link ?}>
     */
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public R<Void> handleException(IllegalArgumentException e) {
        log.error(ExceptionUtil.stacktraceToString(e));
        return R.failed(GLOBAL_ERROR_CODE, serverErrorMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public R<Void> handleException(BindException e) {
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * 参数校验(Valid)异常，将校验失败的所有异常组合成一条错误信息
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public R<Void> handleException(MethodArgumentNotValidException e) {
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * CompletableFuture异常处理
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = {CompletionException.class})
    @ResponseBody
    public R<Void> handleException(CompletionException e) {
        String msg = e.getMessage();
        Throwable cause;
        if ((cause = e.getCause()) != null && cause.getMessage() != null) {
            msg = cause.getMessage();
        }
        String[] split = msg.split(":");
        return R.failed(GLOBAL_ERROR_CODE, split[split.length - 1]);
    }


    /**
     * 包装绑定异常结果
     *
     * @param bindingResult 绑定结果
     * @return 异常结果
     */
    private R<Void> wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (CharSequenceUtil.isNotBlank(msg.toString())) {
                msg.append("，");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
        }

        return R.failed(GLOBAL_ERROR_CODE, msg.toString());
    }

    /**
     * 处理资源找不到异常
     *
     * @param e 异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public R<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("resource not found: {}", e.getResourcePath());
        return R.failed(GLOBAL_ERROR_CODE, "资源不存在");
    }

    /**
     * 处理参数类型不匹配异常
     *
     * @param e e
     * @return r
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleException(MethodArgumentTypeMismatchException e) {
        Throwable cause = e.getCause();
        // 一般嵌套一两次就可以拿到信息了
        String msg = cause.getCause() == null ? cause.getMessage() : cause.getCause().getMessage();
        return R.failed(GLOBAL_ERROR_CODE, msg);
    }

    /**
     * 保留控制器主动声明的 HTTP 状态码。
     *
     * @param e HTTP 状态异常
     * @return 带原始 HTTP 状态码的统一错误结果
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<R<Void>> handleResponseStatusException(ResponseStatusException e) {
        HttpStatusCode status = e.getStatusCode();
        String message = CharSequenceUtil.isBlank(e.getReason()) ? status.toString() : e.getReason();
        return ResponseEntity.status(status).body(R.failed(status.value(), message));
    }

    /**
     * 异常基类处理
     *
     * @param e 异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        Throwable cause;
        String msg;
        if ((cause = e.getCause()) != null) {
            msg = cause.getMessage();
            log.error(msg);
        }
        log.error(ExceptionUtil.stacktraceToString(e));
        return R.failed(GLOBAL_ERROR_CODE, serverErrorMessage());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (ResourceHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return false;
        }
        String declaringClassName = returnType.getDeclaringClass().getName();
        return webProperties.getResponse().getIgnoredClassNamePrefixes().stream().noneMatch(declaringClassName::startsWith);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // SSE is already a framed streaming protocol. Wrapping individual chunks in
        // the common JSON response envelope corrupts the event stream.
        if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(selectedContentType)) {
            return body;
        }
        if (shouldWriteRawBody(request.getURI().getPath())) {
            return body;
        }
        if (body == null) {
            return R.success();
        }
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtil.toJson(R.success(body));
        }
        if (body instanceof R) {
            return body;
        }
        return R.success(body);
    }

    /**
     * 是否写入原生数据而不加包装
     *
     * @param path 路径
     * @return 是否包装
     */
    private boolean shouldWriteRawBody(String path) {
        return webProperties.getResponse().getRawBodyPathPatterns().stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String serverErrorMessage() {
        return webProperties.getResponse().getServerErrorMessage();
    }
}
