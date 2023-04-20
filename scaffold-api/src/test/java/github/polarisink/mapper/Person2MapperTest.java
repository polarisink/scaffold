package github.polarisink.mapper;

import github.polarisink.dao.data.master.PersonMapper;
import github.polarisink.dao.data.master.PersonRepo;
import github.polarisink.dao.data.slave1.Person2;
import github.polarisink.dao.data.slave1.Person2Mapper;
import github.polarisink.dao.data.slave1.Person2Repo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Person2MapperTest {
    @Autowired
    Person2Mapper person2Mapper;
    @Autowired
    Person2Repo person2Repo;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    PersonRepo personRepo;

    /**
     * 测试多数据源下单数据源的事务
     */
    @Test
    void transaction() {
        Person2 person2 = new Person2().setName("lqs").setAge(111);
        person2Mapper.insert(person2);
        person2Repo.saveAndFlush(person2);
        int i = 1/0;
    }
}