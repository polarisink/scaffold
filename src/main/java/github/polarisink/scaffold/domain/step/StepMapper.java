package github.polarisink.scaffold.domain.step;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;


/**
 * stepMapper
 * @author lqs
 */
@Mapper
public interface StepMapper extends BaseMapper<Step> {


  /**
   * 通过机床档案id为空查询指定模板id的步骤列表
   * 这种查询就没有jpa方便，直接声明式查询
   * 使用注解排除警告
   * @param templateId
   * @return
   */
  default List<Step> findAllByArchivesIdIsNullAndTemplateId(Long templateId) {
    LambdaQueryWrapper<Step> wrapper = new LambdaQueryWrapper<>();
    wrapper.isNull(Step::getArchivesId);
    wrapper.eq(Step::getTemplateId, templateId);
    return selectList(wrapper);
  }

  /**
   * 可以方便的做单表动态条件分页查询
   * 但是只适合但表操作，连接其他表直接编写mapper
   * @param step
   * @return
   */
  default IPage<Step> singlePage(Step step) {
    LambdaQueryWrapper<Step> wrapper = new LambdaQueryWrapper<>();
    if (StrUtil.isNotBlank(step.getName())) {
      wrapper.like(Step::getName, step.getName());
    }
    Optional.ofNullable(step.getArchivesId()).ifPresent(archivesId -> wrapper.eq(Step::getArchivesId, archivesId));
    return selectPage(new Page<>(1, 2), wrapper);
  }


}