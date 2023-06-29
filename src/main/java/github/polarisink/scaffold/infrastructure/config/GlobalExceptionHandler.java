package github.polarisink.scaffold.infrastructure.config;


import github.polarisink.scaffold.domain.Response;
import github.polarisink.scaffold.infrastructure.asserts.AssertConst;
import github.polarisink.scaffold.infrastructure.asserts.BaseException;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <p>全局异常处理器</p>
 *
 * @author aries
 * @date 2022/5/2
 */
@Slf4j
@Component
@RestControllerAdvice(basePackages = "cn.hzncc.assembly.resource")
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object> {

    /**
     * 生产环境
     */
    @Value("${spring.profiles.active}")
    private String active;

    private boolean isProd() {
        return Objects.equals(active, "prod");
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
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
     * 自定义异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BaseException.class)
    public Response<Void> handleBaseException(BaseException e) {
        e.printStackTrace();
        return Response.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public Response<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        e.printStackTrace();
        return Response.fail(e.getMessage());
    }


    /**
     * 未定义异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = Exception.class)
    public Response<Void> handleException(Exception e) {
        e.printStackTrace();
        String message = isProd() ? AssertConst.BUSINESS_ERROR_MESSAGE : e.getMessage();
        return Response.fail(message);
    }

    @ExceptionHandler(value = CompletionException.class)
    public Response<Void> handleCompletionException(CompletionException e) {
        e.printStackTrace();
        return Response.fail(e.getCause().getMessage());
    }

    /**
     * 参数异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Response<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        e.printStackTrace();
        return Response.fail(e.getMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public Response<Void> handleBindException(BindException e) {
        e.printStackTrace();
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
        e.printStackTrace();
        return wrapperBindingResult(e.getBindingResult());
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
        return Response.fail(AssertConst.BUSINESS_ERROR_CODE, msg.substring(2));
    }

}
