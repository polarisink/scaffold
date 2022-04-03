package github.polarisink.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import github.polarisink.util.TimeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * JPA实体类基类，逐步替代以后都要使用改基类
 *
 * @author lqs
 * @date 2022/3/21
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class JpaBaseEntity {
    /**
     * 主键id
     */
    @Schema(name = "主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = TimeUtils.sStr)
    @JsonFormat(pattern = TimeUtils.sStr, timezone = "GMT+8")
    @CreatedDate
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = TimeUtils.sStr)
    @JsonFormat(pattern = TimeUtils.sStr)
    @LastModifiedDate
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
