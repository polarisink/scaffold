package github.polarisink.dao.bean.page;

import lombok.Data;

/**
 * 分页查询请求类基类,配合PageUtil
 *
 * @author aries
 * @date 2022/6/21
 */
@Data
public class BasePage {

    protected int page = 1;
    protected int size = 10;
}
