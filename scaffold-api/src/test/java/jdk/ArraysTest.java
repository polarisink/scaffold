package jdk;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author aries
 * @date 2022/8/19
 */
public class ArraysTest {

    @Test
    public void context() {
        List<Object> objects = Arrays.asList(1, 2, Arrays.asList(3, 4, 5));
        System.out.println(objects);
    }
}
