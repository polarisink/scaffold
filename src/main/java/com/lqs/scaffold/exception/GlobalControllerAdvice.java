package com.lqs.scaffold.exception;


import com.lqs.scaffold.core.LangProvider;
import com.lqs.scaffold.core.ResultT;
import com.lqs.scaffold.core.ResultWrapper;
import com.lqs.scaffold.enums.ResponseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

/**
 * 全局异常处理器
 *
 * @author lqs
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

	/**
	 * 处理业务异常
	 *
	 * @param be
	 * @return
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BusinessException.class)
	public ResultT businessException(BusinessException be) {
		log.error("BusinessException:[code:{},message:{}]", be.getCode(), be.getMsg());
		return ResultT.fail(be.msg, be.code);
	}

	/**
	 * 远程调用异常
	 *
	 * @param be
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RemoteException.class)
	public ResultT remoteException(RemoteException be) {
		log.error("RemoteException:[code:{},message:{}]", be.getCode(), be.getMsg());
		return ResultT.fail(be.msg, be.code);
	}

	/**
	 * restTemplate异常处理
	 *
	 * @param be
	 * @return
	 */
	@ExceptionHandler(RestClientException.class)
	public ResultT remoteException(RestClientException be) {
		log.error("RemoteException:[code:{},message:{}]", ResponseEnum.CODE_9000.getCode(), be.getMessage());
		return ResultT.fail("数据获取失败", ResponseEnum.CODE_9000.getCode());
	}

	/**
	 * 处理一般异常
	 *
	 * @param be
	 * @return
	 */
	//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    /*@ExceptionHandler(Exception.class)
    public ResultT exception(Exception be) {
        log.error("Exception:[code:{},message:{}]", ResponseEnum.CODE_9000.getCode(), be.getStackTrace());
        return ResultT.fail(Arrays.toString(be.getStackTrace()));
    }*/

	/**
	 * 处理参数异常
	 *
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResultT parameterExceptionHandler(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException,{}", e.toString());
		/* 获取异常信息 */
		BindingResult exceptions = e.getBindingResult();
		/* 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息 */
		if (exceptions.hasErrors()) {
			List<ObjectError> errors = exceptions.getAllErrors();
			if (!errors.isEmpty()) {
				/* 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可 */
				FieldError fieldError = (FieldError) errors.get(0);
				return ResultT.fail(fieldError.getDefaultMessage(), ResponseEnum.CODE_9000.getCode());
			}
		}
		return ResultT.fail(ResponseEnum.PARAM_ERROR);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BusinessException.class)
	public ResultWrapper handleBusinessException(I18nBusinessException e) throws BadRequestException, IOException {
		String message = LangProvider.getInstance(e.getLang()).getMessage(e.getCode().getLabel());
		return ResultWrapper.of(message, e.getCode());
	}

	/**
	 * json处理异常
	 *
	 * @param e
	 * @return
	 * @throws BadRequestException
	 * @throws IOException
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(JacksonException.class)
	public ResultT jacksonException(JacksonException e) throws BadRequestException, IOException {
		return ResultT.fail(e.msg, e.getCode());
	}
}
