package com.scaffold.rbac.service;

import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysConfig;
import com.scaffold.rbac.mapper.SysConfigMapper;
import com.scaffold.rbac.vo.config.SysConfigCreateVO;
import com.scaffold.rbac.vo.config.SysConfigPageVO;
import com.scaffold.rbac.vo.config.SysConfigUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.scaffold.rbac.contant.RbacCacheConst.CONFIG_DATA_CACHE;
import static com.scaffold.rbac.contant.RbacLogConst.CONFIG;

@Service
@RequiredArgsConstructor
public class SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Transactional(readOnly = true)
    public PageResponse<SysConfig> page(SysConfigPageVO vo) {
        return sysConfigMapper.page(vo);
    }

    @Cacheable(cacheNames = CONFIG_DATA_CACHE, key = "#configKey")
    @Transactional(readOnly = true)
    public SysConfig findByKey(String configKey) {
        SysConfig config = sysConfigMapper.findByConfigKey(configKey);
        RbacResultEnum.CONFIG_NOT_FOUND.notNull(config);
        return config;
    }

    // @formatter:off
    @LogRecord(type = CONFIG,// 大类
            subType = "新增配置",// 小类
            success = "新增配置【{{#vo.configName}}】，配置ID：{{#_ret}}",// 成功日志
            fail = "新增配置【{{#vo.configName}}】失败，原因：{{#_errorMsg}}",// 失败日志
            bizNo = "{{#_ret}}",  // 使用返回值（新配置ID）作为业务编号
            extra = "{{#vo.toString()}}"  // 记录完整的创建请求
    )
    // @formatter:on
    @CacheEvict(cacheNames = CONFIG_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysConfigCreateVO vo) {
        RbacResultEnum.UNIQUE_CONFIG_NAME.isFalse(sysConfigMapper.existsByConfigName(vo.configName()));
        RbacResultEnum.UNIQUE_CONFIG_KEY.isFalse(sysConfigMapper.existsByConfigKey(vo.configKey()));
        SysConfig entity = new SysConfig();
        BeanUtils.copyProperties(vo, entity);
        entity.setSysFlag(Boolean.TRUE.equals(vo.sysFlag()));
        sysConfigMapper.insert(entity);
        return entity.getId();
    }

    // @formatter:off
    @LogRecord(
            type = CONFIG,
            subType = "更新配置",
            success = "更新配置【{{#vo.configName}}】，配置ID：{{#vo.id}}",
            bizNo = "{{#vo.id}}",
            fail = "更新配置【{{#vo.configName}}】失败，原因：{{#_errorMsg}}"
    )
    @CacheEvict(cacheNames = CONFIG_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysConfigUpdateVO vo) {
        SysConfig entity = sysConfigMapper.selectById(vo.id());
        RbacResultEnum.CONFIG_NOT_FOUND.notNull(entity);
        RbacResultEnum.CAN_NOT_MODIFY_SYSTEM_CONFIG_KEY.isTrue(
                !Boolean.TRUE.equals(entity.getSysFlag())
                        || Objects.equals(entity.getConfigKey(), vo.configKey()));
        RbacResultEnum.UNIQUE_CONFIG_NAME.isTrue(
                Objects.equals(entity.getConfigName(), vo.configName())
                        || !sysConfigMapper.existsByConfigName(vo.configName()));
        RbacResultEnum.UNIQUE_CONFIG_KEY.isTrue(
                Objects.equals(entity.getConfigKey(), vo.configKey())
                        || !sysConfigMapper.existsByConfigKey(vo.configKey()));
        boolean systemConfig = Boolean.TRUE.equals(entity.getSysFlag());
        BeanUtils.copyProperties(vo, entity);
        if (systemConfig) {
            entity.setSysFlag(true);
        }
        sysConfigMapper.updateById(entity);
    }


    // @formatter:off
    @LogRecord(
            type = CONFIG,
            subType = "删除配置",
            success = "删除配置，配置ID：{{#configId}}",
            bizNo = "{{#configId}}",
            fail = "删除配置（ID：{{#configId}}）失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @CacheEvict(cacheNames = CONFIG_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long configId) {
        SysConfig entity = sysConfigMapper.selectById(configId);
        RbacResultEnum.CONFIG_NOT_FOUND.notNull(entity);
        RbacResultEnum.CAN_NOT_DELETE_SYSTEM_CONFIG.isFalse(Boolean.TRUE.equals(entity.getSysFlag()));
        sysConfigMapper.deleteById(configId);
    }
}
