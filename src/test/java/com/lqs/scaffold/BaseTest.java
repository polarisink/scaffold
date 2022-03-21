package com.lqs.scaffold;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 抽象测试类，直接继承就可以使用，不用再写重复代码
 *
 * @author lqs
 * @date 2022/3/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class BaseTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

}
