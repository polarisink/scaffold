package com.scaffold.rbac.service;

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

import static com.scaffold.rbac.contant.RbacCacheConst.DICT_DATA;

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

    @CacheEvict(cacheNames = DICT_DATA, allEntries = true)
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

    @CacheEvict(cacheNames = DICT_DATA, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        SysDictType entity = dictTypeMapper.selectById(id);
        RbacResultEnum.DICT_TYPE_NOT_FOUND.notNull(entity);
        RbacResultEnum.CAN_NOT_DELETE_DICT_HAS_DATA.isFalse(dictDataMapper.existsByDictType(entity.getDictType()));
        dictTypeMapper.deleteById(id);
    }
}
