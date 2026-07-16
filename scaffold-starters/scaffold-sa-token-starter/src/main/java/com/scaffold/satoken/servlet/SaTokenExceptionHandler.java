package com.scaffold.satoken.servlet;

import cn.dev33.satoken.exception.NotLoginException;
import com.scaffold.base.util.R;
import com.scaffold.security.vo.AuthCodeEnum;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<R<Void>> handleNotLoginException(NotLoginException exception) {
        AuthCodeEnum reason = switch (exception.getType()) {
            case NotLoginException.TOKEN_TIMEOUT -> AuthCodeEnum.TOKEN_EXPIRED;
            case NotLoginException.INVALID_TOKEN, NotLoginException.NO_PREFIX -> AuthCodeEnum.TOKEN_INVALID;
            default -> AuthCodeEnum.UNAUTHORIZED;
        };
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(R.failed(reason.getCode(), reason.getMessage()));
    }
}
