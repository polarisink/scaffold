package github.polarisink.common;

/**
 * <pre>
 *  异常返回码枚举接口
 * </pre>
 *
 * @author aries
 * @date 2022/5/2
 */
public interface IResponseEnum {
  /**
   * 获取返回码
   *
   * @return 返回码
   */
  int getCode();

  /**
   * 获取返回信息
   *
   * @return 返回信息
   */
  String getMessage();
}
