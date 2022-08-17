package github.polarisink.scaffold.common.exception;

import cn.hutool.core.util.StrUtil;
import github.polarisink.scaffold.common.IResponseEnum;
import github.polarisink.scaffold.common.enums.BaseE;
import lombok.Getter;

import java.io.Serializable;

/**
 * <p>基础异常类，所有自定义异常类都需要继承本类</p>
 *
 * @author aries
 * @date 2022/5/2
 */
@Getter
public class BaseException extends RuntimeException implements Serializable {


  private static final long serialVersionUID = 3357045510642536389L;
  /**
   * 返回码
   */
  protected final IResponseEnum responseEnum;
  /**
   * 异常消息参数
   */
  protected final Object[] args;

  public BaseException(IResponseEnum responseEnum) {
    super(responseEnum.getMessage());
    this.args = null;
    this.responseEnum = responseEnum;
  }

  public BaseException(int code, String msg) {
    super(msg);
    this.args = null;
    this.responseEnum = new IResponseEnum() {
      @Override
      public int getCode() {
        return code;
      }

      @Override
      public String getMessage() {
        return msg;
      }
    };
  }

  public BaseException(IResponseEnum responseEnum, Object[] args, String message) {
    super(message);
    this.responseEnum = responseEnum;
    this.args = args;
  }

  public BaseException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
    super(cause.getMessage(), cause);
    this.responseEnum = responseEnum;
    this.args = args;
  }

  public BaseException(String msg) {
    super(msg);
    this.args = null;
    this.responseEnum = new IResponseEnum() {
      @Override
      public int getCode() {
        return BaseE.BASE.getCode();
      }

      @Override
      public String getMessage() {
        return msg;
      }
    };
  }

  public BaseException(String format, Object... args) {
    super(StrUtil.format(format, args));
    this.args = args;
    this.responseEnum = new IResponseEnum() {
      @Override
      public int getCode() {
        return BaseE.BASE.getCode();
      }

      @Override
      public String getMessage() {
        return StrUtil.format(format, args);
      }
    };
  }

  /*public BaseException() {
    super();
    this.args = null;
    this.responseEnum = new IResponseEnum() {
      @Override
      public int getCode() {
        return BaseResponseEnum.Base.getCode();
      }
      @Override
      public String getMessage() {
        return BaseResponseEnum.Base.getMessage();
      }
    };
  }*/
}
