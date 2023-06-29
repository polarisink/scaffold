package github.polarisink.scaffold.infrastructure.asserts;

/**
 * <pre>
 *  返回码枚举接口
 * </pre>
 *
 * @author aries
 * @date 2022/5/2
 */
public interface BaseEnum {

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
