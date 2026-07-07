package com.scaffold.rbac.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysConfig;
import com.scaffold.rbac.vo.config.SysConfigPageVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysConfigMapper extends MyBaseMapper<SysConfig> {

    default boolean existsByConfigName(String configName) {
        return exists(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getConfigName, configName));
    }

    default boolean existsByConfigKey(String configKey) {
        return exists(Wrappers.<SysConfig>lambdaQuery().eq(SysConfig::getConfigKey, configKey));
    }

    default SysConfig findByConfigKey(String configKey) {
        return selectOne(Wrappers.<SysConfig>lambdaQuery()
                .eq(SysConfig::getConfigKey, configKey)
                .last("limit 1"));
    }

    default PageResponse<SysConfig> page(SysConfigPageVO vo) {
        Page<SysConfig> page = selectPage(
                new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysConfig>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getConfigName()), SysConfig::getConfigName, vo.getConfigName())
                        .like(StrUtil.isNotBlank(vo.getConfigKey()), SysConfig::getConfigKey, vo.getConfigKey())
                        .orderByDesc(SysConfig::getGmtModified));
        return PageUtils.of(page);
    }
}
