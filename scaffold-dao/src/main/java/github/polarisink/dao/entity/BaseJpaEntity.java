package github.polarisink.dao.entity;

import static github.polarisink.common.utils.TimeUtils.sFStr;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  @CreatedDate
  protected LocalDateTime createTime;

  @DateTimeFormat(pattern = sFStr)
  @JsonFormat(pattern = sFStr, timezone = "GMT+8")
  @LastModifiedDate
  protected LocalDateTime updateTime;

}
