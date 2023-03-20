package jdk;

import org.junit.Test;

import java.util.Objects;

/**
 * @author aries
 * @date 2022/8/23
 */

public class ObjectsTest {

    @Test
    public void context() {
        String a = null;
        String b = null;
        System.out.println(Objects.equals(a, b));
    }

}
