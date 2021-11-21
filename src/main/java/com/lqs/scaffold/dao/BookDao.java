package com.lqs.scaffold.dao;

import com.lqs.scaffold.entity.Books;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 书 Mapper 接口
 * </p>
 *
 * @author polaris
 * @since 2021-11-10
 */
@Mapper
public interface BookDao extends BaseMapper<Books> {

}
