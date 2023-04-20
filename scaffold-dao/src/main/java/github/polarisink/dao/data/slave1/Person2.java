package github.polarisink.dao.data.slave1;

import com.baomidou.mybatisplus.annotation.TableName;
import github.polarisink.dao.data.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "person2")
@Entity(name = "person2")
@Data
@TableName("person2")
public class Person2 extends BaseEntity {
    private String name;
    private Integer age;
    private String address;

    private String goodAt;

}
