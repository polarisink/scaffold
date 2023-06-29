package github.polarisink.scaffold.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * JPA实体类基类，逐步替代以后都要使用改基类
 *
 * @author lqs
 * @date 2022/3/21
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseJpaEntity implements Serializable {

    private static final long serialVersionUID = 733899366518016549L;
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    protected Long id;


    //不指定columnDefinition会默认使用datetime(6)，导致报错
    @CreatedDate
    @Column(columnDefinition = "datetime comment '创建时间'")
    protected LocalDateTime createTime;


    @LastModifiedDate
    @Column(columnDefinition = "datetime comment '更新时间'")
    protected LocalDateTime updateTime;

}
