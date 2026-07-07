package com.scaffold.rbac.service;

import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysDictType;
import com.scaffold.rbac.mapper.SysDictDataMapper;
import com.scaffold.rbac.mapper.SysDictTypeMapper;
import com.scaffold.rbac.vo.dict.SysDictTypeCreateVO;
import com.scaffold.rbac.vo.dict.SysDictTypePageVO;
import com.scaffold.rbac.vo.dict.SysDictTypeUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.scaffold.rbac.contant.RbacCacheConst.DICT_DATA_CACHE;
import static com.scaffold.rbac.contant.RbacLogConst.DICT_TYPE;

@Service
@RequiredArgsConstructor
public class SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;

    @Transactional(readOnly = true)
    public PageResponse<SysDictType> page(SysDictTypePageVO vo) {
        return dictTypeMapper.page(vo);
    }

    @Transactional(readOnly = true)
    public List<SysDictType> options() {
        return dictTypeMapper.listEnabled();
    }

    // @formatter:off
    @LogRecord(
            type = DICT_TYPE,// 大类
            subType = "新增字典类型",// 小类
            success = "新增字典类型【{{#vo.dictName}}】，字典类型ID：{{#_ret}}",// 成功日志
            fail = "新增字典类型【{{#vo.dictName}}】失败，原因：{{#_errorMsg}}",// 失败日志
            bizNo = "{{#_ret}}",  // 使用返回值（新字典类型ID）作为业务编号
            extra = "{{#vo.toString()}}"  // 记录完整的创建请求
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysDictTypeCreateVO vo) {
        RbacResultEnum.UNIQUE_DICT_NAME.isFalse(dictTypeMapper.existsByDictName(vo.dictName()));
        RbacResultEnum.UNIQUE_DICT_TYPE.isFalse(dictTypeMapper.existsByDictType(vo.dictType()));
        SysDictType entity = new SysDictType();
        BeanUtils.copyProperties(vo, entity);
        entity.setStatus(vo.status() == null || vo.status());
        dictTypeMapper.insert(entity);
        return entity.getId();
    }

    // @formatter:off
    @LogRecord(
            type = DICT_TYPE,
            success = "更新字典类型【{{#vo.dictName}}】，字典类型ID：{{#vo.id}}",
            subType = "更新字典类型",
            bizNo = "{{#vo.id}}",
            fail = "更新字典类型【{{#vo.dictName}}】失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @CacheEvict(cacheNames = DICT_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysDictTypeUpdateVO vo) {
        SysDictType entity = dictTypeMapper.selectById(vo.id());
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(entity);
        RbacResultEnum.UNIQUE_DICT_NAME.isTrue(Objects.equals(entity.getDictName(), vo.dictName())
                || !dictTypeMapper.existsByDictName(vo.dictName()));
        RbacResultEnum.UNIQUE_DICT_TYPE.isTrue(Objects.equals(entity.getDictType(), vo.dictType())
                || !dictTypeMapper.existsByDictType(vo.dictType()));
        String oldType = entity.getDictType();
        BeanUtils.copyProperties(vo, entity);
        entity.setStatus(vo.status() == null || vo.status());
        dictTypeMapper.updateById(entity);
        if (!Objects.equals(oldType, entity.getDictType())) {
            dictDataMapper.updateDictType(oldType, entity.getDictType());
        }
    }

    // @formatter:off
    @LogRecord(
            type = DICT_TYPE,
            success = "删除字典类型，字典类型ID：{{#id}}",
            subType = "删除字典类型",
            bizNo = "{{#id}}",
            fail = "删除字典类型（ID：{{#id}}）失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @CacheEvict(cacheNames = DICT_DATA_CACHE, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        SysDictType entity = dictTypeMapper.selectById(id);
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(entity);
        RbacResultEnum.CAN_NOT_DELETE_DICT_HAS_DATA.isFalse(dictDataMapper.existsByDictType(entity.getDictType()));
        dictTypeMapper.deleteById(id);
    }
}
