package github.polarisink.scaffold.domain.step;


import com.baomidou.mybatisplus.annotation.TableName;
import github.polarisink.scaffold.domain.BaseJpaEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * 步骤实体类
 * 有逻辑删除功能，需要忽略deleted的时候需要手写原生sql
 *
 * @author hzsk
 */
@Data
@NoArgsConstructor
@Table(name = "step", indexes = {
        @Index(name = "aId_tId_index", columnList = "archivesId"),
        @Index(name = "aId_tId_index", columnList = "templateId"),
        @Index(name = "pId_index", columnList = "parentId"),
})
@Entity(name = "step")
@TableName("step")
public class Step extends BaseJpaEntity implements Comparable<Step>, Serializable {

    private Long archivesId;

    /**
     * 步骤所在模板ID
     */
    private Long templateId;

    /**
     * 父步骤ID
     */
    private Long parentId;


    /**
     * 排序,从0开始进行排序
     */
    private Integer orderNum;

    /**
     * 步骤名字
     */
    private String name;

    /**
     * 层级，root的层级为1，子节点+1
     * 创建之后就不能修改了
     */
    @Column(updatable = false)
    private Integer level;


    /**
     * 是否为步骤，为了兼容前端老代码写的一个定值，平时忽略他即可，不需要做多的操作
     */
    @Transient
    private Boolean isStep = true;

    /**
     * 子步骤list,数据库不存在
     */
    @Transient
    private List<Step> children;


    //parentId,name,orderNum,level
    public Step(Long parentId, String name, Integer orderNum, Integer level) {
        this.name = name;
        this.parentId = parentId;
        this.orderNum = orderNum;
        this.level = level;
    }

    /**
     * 都不为空，比较orderNum
     * 都为空，比较更新时间
     * 否则谁不为空谁在前面
     *
     * @param o the object to be compared.
     * @return
     */


    @Override
    public int compareTo(@NotNull Step o) {
        if (this.orderNum != null && o.orderNum != null) {
            return this.orderNum - o.orderNum;
        }
        if (this.orderNum == null && o.orderNum == null) {
            return this.updateTime.compareTo(o.updateTime);
        }
        return this.orderNum == null ? -1 : 1;
    }
}
