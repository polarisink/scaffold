package com.scaffold.flink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCountJobTest {

    @Test
    void tokenizerNormalizesWordsAndIgnoresSeparators() throws Exception {
        List<Tuple2<String, Integer>> result = new ArrayList<>();
        Collector<Tuple2<String, Integer>> collector = new ListCollector(result);

        new WordCountJob.Tokenizer().flatMap("Hello, FLINK!  ", collector);

        assertEquals(List.of(Tuple2.of("hello", 1), Tuple2.of("flink", 1)), result);
    }

    @Test
    void textArgumentOverridesDefaultInput() {
        assertEquals("one two", WordCountJob.resolveText(new String[]{"--text", "one two"}));
    }

    private record ListCollector(List<Tuple2<String, Integer>> values)
            implements Collector<Tuple2<String, Integer>> {

        @Override
        public void collect(Tuple2<String, Integer> value) {
            values.add(value);
        }

        @Override
        public void close() {
            // 无外部资源需要释放。
        }
    }
}
