package com.scaffold.rbac.service;

import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.contant.RbacLogConst;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysDictData;
import com.scaffold.rbac.mapper.SysDictDataMapper;
import com.scaffold.rbac.mapper.SysDictTypeMapper;
import com.scaffold.rbac.vo.dict.SysDictDataCreateVO;
import com.scaffold.rbac.vo.dict.SysDictDataPageVO;
import com.scaffold.rbac.vo.dict.SysDictDataUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.scaffold.rbac.contant.RbacCacheConst.DICT_DATA_CACHE;
import static com.scaffold.rbac.contant.RbacLogConst.DICT_DATA;

@Service
@RequiredArgsConstructor
public class SysDictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final SysDictTypeMapper dictTypeMapper;

    @Transactional(readOnly = true)
    public PageResponse<SysDictData> page(SysDictDataPageVO vo) {
        return dictDataMapper.page(vo);
    }

    @Cacheable(cacheNames = DICT_DATA_CACHE, key = "#dictType")
    @Transactional(readOnly = true)
    public List<SysDictData> listByType(String dictType) {
        var type = dictTypeMapper.findByDictType(dictType);
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(type);
        if (!Boolean.TRUE.equals(type.getStatus())) {
            return List.of();
        }
        return dictDataMapper.listEnabledByType(dictType);
    }

    // @formatter:off
    @LogRecord(
            type = DICT_DATA,// 大类
            subType = "新增字典数据",// 小类
            success = "新增字典数据【{{#vo.dictLabel}}】，字典数据ID：{{#_ret}}",// 成功日志
            fail = "新增字典数据【{{#vo.dictLabel}}】失败，原因：{{#_errorMsg}}",// 失败日志
            bizNo = "{{#_ret}}",  // 使用返回值（新字典数据ID）作为业务编号
            extra = "{{#vo.toString()}}"  // 记录完整的创建请求
    )
    // @formatter:on
    @CacheEvict(cacheNames = DICT_DATA_CACHE, key = "#vo.dictType()")
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysDictDataCreateVO vo) {
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(dictTypeMapper.findByDictType(vo.dictType()));
        RbacResultEnum.UNIQUE_DICT_LABEL.isFalse(dictDataMapper.existsByLabel(vo.dictType(), vo.dictLabel()));
        RbacResultEnum.UNIQUE_DICT_VALUE.isFalse(dictDataMapper.existsByValue(vo.dictType(), vo.dictValue()));
        SysDictData entity = new SysDictData();
        BeanUtils.copyProperties(vo, entity);
        normalize(entity, vo.status(), vo.defaultFlag(), vo.dictSort());
        if (entity.getDefaultFlag()) {
            dictDataMapper.clearDefault(entity.getDictType(), null);
        }
        dictDataMapper.insert(entity);
        return entity.getId();
    }

    // @formatter:off
    @LogRecord(
            type = DICT_DATA,
            success = "更新字典数据【{{#vo.dictLabel}}】，字典数据ID：{{#vo.id}}",
            subType = "更新字典数据",
            bizNo = "{{#vo.id}}",
            fail = "更新字典数据【{{#vo.dictLabel}}】失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @CacheEvict(cacheNames = DICT_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysDictDataUpdateVO vo) {
        SysDictData entity = dictDataMapper.selectById(vo.id());
        RbacResultEnum.DICT_DATA_NOT_FOUND.notNull(entity);
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(dictTypeMapper.findByDictType(vo.dictType()));
        boolean sameType = Objects.equals(entity.getDictType(), vo.dictType());
        RbacResultEnum.UNIQUE_DICT_LABEL.isTrue(
                sameType && Objects.equals(entity.getDictLabel(), vo.dictLabel())
                        || !dictDataMapper.existsByLabel(vo.dictType(), vo.dictLabel()));
        RbacResultEnum.UNIQUE_DICT_VALUE.isTrue(
                sameType && Objects.equals(entity.getDictValue(), vo.dictValue())
                        || !dictDataMapper.existsByValue(vo.dictType(), vo.dictValue()));
        BeanUtils.copyProperties(vo, entity);
        normalize(entity, vo.status(), vo.defaultFlag(), vo.dictSort());
        if (entity.getDefaultFlag()) {
            dictDataMapper.clearDefault(entity.getDictType(), entity.getId());
        }
        dictDataMapper.updateById(entity);
    }

    // @formatter:off
    @LogRecord(
            type = DICT_DATA,
            success = "删除字典数据，字典数据ID：{{#id}}",
            subType = "删除字典数据",
            bizNo = "{{#id}}",
            fail = "删除字典数据（ID：{{#id}}）失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @CacheEvict(cacheNames = DICT_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        RbacResultEnum.DICT_DATA_NOT_FOUND.notNull(dictDataMapper.selectById(id));
        dictDataMapper.deleteById(id);
    }

    private static void normalize(SysDictData entity, Boolean status, Boolean defaultFlag, Integer sort) {
        entity.setStatus(status == null || status);
        entity.setDefaultFlag(Boolean.TRUE.equals(defaultFlag));
        entity.setDictSort(sort == null ? 0 : sort);
    }
}
