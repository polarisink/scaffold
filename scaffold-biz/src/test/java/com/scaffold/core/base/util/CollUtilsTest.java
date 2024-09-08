package com.scaffold.core.base.util;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CollUtilsTest {
    @Test
    void sort() {
        List<String> strings = List.of("111", "333", "222");
        strings.sort(Comparator.naturalOrder());
        System.out.println(strings);
    }

    @Test
    void groupBy() {
        List<Integer> list = IntStream.range(0, 10000).boxed().toList();
        Map<Integer, List<String>> map1 = list.stream().collect(Collectors.groupingBy(i -> i % 3, Collectors.collectingAndThen(Collectors.toList(), l -> {
            l.sort(Comparator.naturalOrder());
            return l.stream().map(Object::toString).toList();
        })));
        //map1.get(1).add("hhhhhhhhhhhhhhh");

        Map<Integer, List<Integer>> map = CollUtils.groupBy(list, i -> i % 3);
        map.remove(1);
        List<Integer> integers = map.get(0);
        integers.add(1111111111);
    }
}