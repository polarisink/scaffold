package jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author aries
 * @date 2022/9/22
 */
@Slf4j
public class CollectorsTest {

    List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

    @Test
    public void partitioningBy() {
        Map<Boolean, List<Integer>> map = list.stream().collect(Collectors.partitioningBy(i -> i >= 10));
        map.forEach((b, v) -> System.out.printf("bool: %s, size:%d, values:%s\n", b, v.size(), v));
    }
}
