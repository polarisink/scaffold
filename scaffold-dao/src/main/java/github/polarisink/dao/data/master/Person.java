package github.polarisink.dao.data.master;

import com.baomidou.mybatisplus.annotation.TableName;
import github.polarisink.dao.data.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "person")
@Entity(name = "person")
@Data
@TableName("person")
public class Person extends BaseEntity {
    private String name;
    private Integer age;
    private String address;

    private String goodAt;

}
