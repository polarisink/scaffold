package github.polarisink.mapper;

import cn.hutool.core.lang.id.NanoId;
import github.polarisink.dao.entity.Person;
import github.polarisink.dao.mapper.PersonMapper;
import github.polarisink.dao.mapper.PersonRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PersonRepoTest {
    @Autowired
    PersonRepo personRepo;

    @Autowired
    PersonMapper personMapper;

    @Test
    void insert() {
        personRepo.save(new Person().setName("lqs"));
        personRepo.saveAndFlush(new Person().setName("hyp"));
        personMapper.insert(new Person().setName("csy"));
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    void transactional() {
        personRepo.save(new Person().setName("lqs"));
        personRepo.saveAndFlush(new Person().setName("hyp"));
        if (true) {
            throw new RuntimeException("runtime");
        }
        personMapper.insert(new Person().setName("csy"));
    }

    /**
     *  批量插入非常有用
     *  [StopWatch '1 watch': running time = 789944101 ns; [mbp batch insert 1] took 649349701 ns = 82%; [jpa batch insert 1] took 140594400 ns = 18%,
     *  StopWatch '10 watch': running time = 170492400 ns; [mbp batch insert 10] took 20213400 ns = 12%; [jpa batch insert 10] took 150279000 ns = 88%,
     *  StopWatch '20 watch': running time = 885318800 ns; [mbp batch insert 20] took 28982400 ns = 3%; [jpa batch insert 20] took 856336400 ns = 97%,
     *  StopWatch '50 watch': running time = 1340828101 ns; [mbp batch insert 50] took 24940201 ns = 2%; [jpa batch insert 50] took 1315887900 ns = 98%,
     *  StopWatch '100 watch': running time = 1585173299 ns; [mbp batch insert 100] took 33307999 ns = 2%; [jpa batch insert 100] took 1551865300 ns = 98%,
     *  StopWatch '500 watch': running time = 12121661300 ns; [mbp batch insert 500] took 80988801 ns = 1%; [jpa batch insert 500] took 12040672499 ns = 99%,
     *  StopWatch '1000 watch': running time = 16472742901 ns; [mbp batch insert 1000] took 145699000 ns = 1%; [jpa batch insert 1000] took 16327043901 ns = 99%,
     *  StopWatch '5000 watch': running time = 75874720300 ns; [mbp batch insert 5000] took 717915600 ns = 1%; [jpa batch insert 5000] took 75156804700 ns = 99%]
     */
    @Test
    void batchInsert() {
        Arrays.asList(1, 10, 20, 50, 100, 500, 1000, 5000).stream().map(this::timeCount).toList().forEach(watch -> LOG.info(String.valueOf(watch)));
    }

    StopWatch timeCount(int count) {
        StopWatch watch = new StopWatch(count + " watch");
        List<Person> personList = IntStream.rangeClosed(0, count).mapToObj(i -> new Person().setAge(i).setName(NanoId.randomNanoId())).collect(Collectors.toList());
        watch.start("mbp batch insert " + count);
        personMapper.insertBatchSomeColumn(personList);
        watch.stop();
        watch.start("jpa batch insert " + count);
        personRepo.saveAllAndFlush(personList);
        watch.stop();
        return watch;
        //Arrays.stream(watch.getTaskInfo()).forEach(info -> LOG.info("Task {} consumed {} ms: ", info.getTaskName(), info.getTimeMillis()));
    }
}