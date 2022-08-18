package github.polarisink.common.assertion;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import github.polarisink.common.exception.BaseException;
import github.polarisink.common.IResponseEnum;

/**
 * <p>业务异常断言</p>
 *
 * @author aries
 * @date 2022/5/2
 */
public interface BaseExceptionAssert extends IResponseEnum, Assert {

  /**
   * @param args
   * @return
   */
  @Override
  default BaseException newException(Object... args) {
    String msg = this.getMessage();
    if (ArrayUtil.isNotEmpty(args)) {
      msg = StrUtil.format(this.getMessage(), args);
    }
    return new BaseException(this, args, msg);
  }

  /**
   * @param t
   * @param args
   * @return
   */
  @Override
  default BaseException newException(Throwable t, Object... args) {
    String msg = this.getMessage();
    if (ArrayUtil.isNotEmpty(args)) {
      msg = StrUtil.format(this.getMessage(), args);
    }
    return new BaseException(this, args, msg, t);
  }

}
