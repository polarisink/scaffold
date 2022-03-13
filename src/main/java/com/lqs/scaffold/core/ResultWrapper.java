package com.lqs.scaffold.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lqs.scaffold.enums.HttpCode;
import com.lqs.scaffold.exception.BadRequestException;

import java.io.IOException;

/**
 * Result Wrapper
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public class ResultWrapper<T> {
  private Integer result;
  private T data;
  private String message;

  public Integer getResult() {
    return result;
  }

  public void setResult(Integer result) {
    this.result = result;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public static <T> ResultWrapper<T> of(String lang, HttpCode code, T data)
    throws BadRequestException {
    ResultWrapper<T> resultWrapper = new ResultWrapper<>();
    resultWrapper.setResult(code.getCode());
    resultWrapper.setData(data);
    String message;
    try {
      message = LangProvider.getInstance(lang).getMessage(code.getLabel());
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
    resultWrapper.setMessage(message);
    return resultWrapper;
  }

  public static <T> ResultWrapper<T> of(String lang, HttpCode code)
    throws BadRequestException {
    return of(lang, code, null);
  }

  public static <T> ResultWrapper<T> of(HttpCode code, String message) {
    ResultWrapper<T> resultWrapper = new ResultWrapper<>();
    resultWrapper.setResult(code.getCode());
    resultWrapper.setMessage(message);
    return resultWrapper;
  }

  public static <T> ResultWrapper<T> success(String lang, T data)
    throws BadRequestException {
    return of(lang, HttpCode.OK, data);
  }


}
