package jdk;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author aries
 * @date 2022/8/15
 */
public class SynTest {

    public static void main(String[] args) {
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5");
        strings.forEach(
                s -> new Thread(() -> IntStream.rangeClosed(6, 9).forEach(i -> new Thread(() -> syn(s), s + "-" + i).start()),
                        s).start());
    }

    public static void syn(String s) {
        synchronized (s) {
            System.out.println(Thread.currentThread().getName() + "-" + "被锁住了");
        }
    }


}
