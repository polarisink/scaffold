package github.polarisink.exception;

import com.google.common.base.Throwables;
import github.polarisink.core.ResultWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-30
 */
@Slf4j
@RestControllerAdvice(basePackages = "jooz.warden.controller")
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	public String badRequest(BadRequestException e) {
		log.error("[ARCHMAGE ERR] ", e);
		return Throwables.getRootCause(e).getMessage();
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(AuthorizationException.class)
	public ResultWrapper unauthorized(AuthorizationException e) throws BadRequestException {
		log.error("[ARCHMAGE ERR] ", e);
		return ResultWrapper.fail(e);
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BaseException.class)
	public ResultWrapper myBaseException(BaseException e) throws BadRequestException {
		log.error("[ARCHMAGE ERR] ", e);
		return ResultWrapper.fail(e);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResultWrapper invalidParameter(
		MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		String errMsg = errors.toString();
		log.error("[ARCHMAGE ERR] {}", errMsg);
		return ResultWrapper.fail(HttpCode.INVALID_PARAMETER, errMsg);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BusinessException.class)
	public ResultWrapper handleBusinessException(BusinessException e) throws BadRequestException, IOException {
		return ResultWrapper.fail(e);
	}
}
