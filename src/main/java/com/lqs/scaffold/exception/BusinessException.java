package com.lqs.scaffold.exception;

import com.lqs.scaffold.enums.HttpCode;
import com.lqs.scaffold.enums.LangTypes;

/**
 * @author lqs
 * @describe
 * @date 2021/11/6
 */
public class BusinessException extends BaseException {

	protected BusinessException(HttpCode httpCode){
		super(LangTypes.EN_US.name(),httpCode);
	}

	protected BusinessException(String lang, HttpCode httpCode) {
		super(lang, httpCode);
	}
	
}
