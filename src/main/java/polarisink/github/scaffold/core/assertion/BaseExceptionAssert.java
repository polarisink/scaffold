package polarisink.github.scaffold.core.assertion;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import polarisink.github.scaffold.core.Assert;
import polarisink.github.scaffold.core.IResponseEnum;
import polarisink.github.scaffold.core.exception.BaseException;

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
