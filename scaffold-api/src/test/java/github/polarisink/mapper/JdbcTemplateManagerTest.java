package github.polarisink.mapper;

import github.polarisink.dao.entity.Person;
import github.polarisink.dao.mapper.JdbcTemplateManager;
import github.polarisink.dao.mapper.PersonMapper;
import github.polarisink.dao.mapper.PersonRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class JdbcTemplateManagerTest {
    @Autowired
    JdbcTemplateManager jdbcTemplateManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    PersonRepo personRepo;

    @Test
    void addPersons() {

        int n = 99;
        List<Object[]> list = IntStream.rangeClosed(0, n).mapToObj(i -> new Object[]{"name", i + 10, "hubei" + i, i / 2 == 0 ? "唱跳rap" : "篮球"}).collect(Collectors.toList());
        List<Person> jdbcList = IntStream.rangeClosed(0, n).mapToObj(i -> new Person().setName("jdbcTemplate").setAge(i + 10).setAddress("hubei" + i).setGoodAt(i / 2 == 0 ? "唱跳rap" : "篮球")).collect(Collectors.toList());
        List<Person> jdbcList2 = IntStream.rangeClosed(0, n).mapToObj(i -> new Person().setName("jdbcTemplate2").setAge(i + 10).setAddress("hubei" + i).setGoodAt(i / 2 == 0 ? "唱跳rap" : "篮球")).collect(Collectors.toList());
        List<Person> mbpList = IntStream.rangeClosed(0, n).mapToObj(i -> new Person().setName("mbp").setAge(i + 10).setAddress("hubei" + i).setGoodAt(i / 2 == 0 ? "唱跳rap" : "篮球")).collect(Collectors.toList());
        jdbcTemplate.execute("truncate table person");
        StopWatch watch = new StopWatch();

        watch.start("jdbcTemplate batch1");
        int[] ints = jdbcTemplateManager.addPersons(list);
        watch.stop();

        watch.start("jdbcTemplate batch2");
        int[][] ints2 = jdbcTemplateManager.addPersons2(jdbcList);
        watch.stop();

        watch.start("jpa saveAllAndFlush");
        List<Person> flush = personRepo.saveAllAndFlush(jdbcList2);
        watch.stop();

        watch.start("mybatisPlus batch");
        Integer batchSomeColumn = personMapper.insertBatchSomeColumn(mbpList);
        watch.stop();

        System.out.println(watch.prettyPrint());
    }
}