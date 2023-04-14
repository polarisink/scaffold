package github.polarisink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "person")
@Entity(name = "person")
@Data
@TableName
public class Person extends BaseEntity {
    private String name;
    private Integer age;
    private String address;

    @Column(name = "good_at")
    private String goodAt;
}
