package github.polarisink.dao.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * JPA实体类基类，逐步替代以后都要使用改基类
 *
 * @author lqs
 * @date 2022/3/21
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

  private static final long serialVersionUID = 733899366518016549L;
  /**
   * 主键id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @TableId(type = IdType.AUTO)
  protected Long id;

  @CreatedDate
  @TableField(fill = FieldFill.INSERT)
  @Column(name = "create_time")
  protected LocalDateTime createTime;

  @Column(name = "update_time")
  @LastModifiedDate
  @TableField(fill = FieldFill.INSERT_UPDATE)
  protected LocalDateTime updateTime;

}
