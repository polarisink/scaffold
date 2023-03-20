package jdk;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author aries
 * @date 2022/9/28
 */
public class StreamTest {

    @Test
    public void test() {
        LinkedHashSet<Integer> set = IntStream.rangeClosed(0, 100).boxed()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        System.out.println(List.of("a", "a", "ca", "t").stream().collect(Collectors.joining()));

    }

    @Test
    public void testSort() {
        //默认从小到大排序

        System.out.println(List.of("audh", "cidj", "csihd").stream().collect(Collectors.joining()));
        List<Integer> collect = List.of(0, -1, 3, 5, -11).stream().sorted().collect(Collectors.toList());
        //System.out.println(collect);
    }
}
