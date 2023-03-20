package github.polarisink.dao.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;


    @CreatedDate
    protected LocalDateTime createTime;

    @LastModifiedDate
    protected LocalDateTime updateTime;

}
