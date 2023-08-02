package github.polarisink.scaffold.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * 分页返回体
 * @author lqs
 * @date 2023/8/2
 */
public class PageResponse<T> {

  private Long total;
  private List<T> records;

  public static <T> PageResponse<T> of(IPage<T> page) {
    PageResponse<T> res = new PageResponse<>();
    res.records = page.getRecords();
    res.total = page.getTotal();
    return res;
  }

  public static <T> PageResponse<T> of(Page<T> page) {
    PageResponse<T> res = new PageResponse<>();
    res.records = page.getContent();
    res.total = page.getTotalElements();
    return res;
  }

}
