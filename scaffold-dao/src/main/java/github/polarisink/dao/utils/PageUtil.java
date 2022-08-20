package github.polarisink.dao.utils;

import com.querydsl.core.QueryResults;
import github.polarisink.dao.bean.page.BasePage;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * SpringData page简化版,方便使用
 *
 * @author lqs
 * @date 2022/3/21
 */
@Data
public class PageUtil<T> implements Serializable {
  private static final long serialVersionUID = -1560448335342382268L;
  private Long total;
  private List<T> rows;

  /**
   * 将springData复杂page对象转换为简单page
   *
   * @param page
   * @param <T>
   * @return
   */
  public static <T> PageUtil<T> of(Page<T> page) {
    if (Objects.isNull(page)) {
      return of(0L, new ArrayList<>());
    }
    return of(page.getTotalElements(), page.getContent());
  }

  /**
   * 封装一个page对象
   *
   * @param total
   * @param content
   * @param <T>
   * @return
   */
  public static <T> PageUtil<T> of(Long total, List<T> content) {
    PageUtil<T> util = new PageUtil<>();
    util.setTotal(total);
    util.setRows(content);
    return util;
  }

  /**
   * QueryDSL分页查询结果包装
   *
   * @param results
   * @param <T>
   * @return
   */
  public static <T> PageUtil<T> of(QueryResults<T> results) {
    return of(results.getTotal(), results.getResults());
  }

  /**
   * 返回空分页对象
   *
   * @param <T>
   * @return
   */
  public static <T> PageUtil<T> of() {
    return of(0L, Collections.emptyList());
  }

  /**
   * 获取基本的分页请求
   *
   * @param t
   * @param <E>
   * @return
   */
  public static <E extends BasePage> Pageable getPageRequest(E t) {
    return getPageRequest(t, Sort.unsorted());
  }

  /**
   * 有排序的分页请求
   *
   * @param e
   * @param sort
   * @param <E>
   * @return
   */
  public static <E extends BasePage> Pageable getPageRequest(E e, Sort sort) {
    return PageRequest.of(e.getPage() - 1, e.getSize(), sort);
  }

  /**
   * 简单判空
   *
   * @return
   */
  public boolean isEmpty() {
    return this.total == 0 || this.rows == null || this.rows.size() == 0;
  }

  /**
   * 完整SpringData Page所有的属性(可用于后期扩展):
   * content:[,,,,],//20items
   * pageable:{
   *     sort:{
   *         sorted:true,
   *         unsorted:false
   *     },
   *     offset:0,
   *     pageNumber:0,
   *     pageSize:20,
   *     paged:true,
   *     unpaged:false
   * },
   * totalElements:124,
   * totalPages:7,
   * last:false,
   * size:20,
   * number:0,
   * numberOfElements:20,
   * sort:{
   *     sorted:true,
   *     unsorted:false
   * },
   * first:true
   */


}

