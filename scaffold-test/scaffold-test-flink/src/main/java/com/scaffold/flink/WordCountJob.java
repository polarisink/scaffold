package com.scaffold.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.Locale;

/**
 * 最小 DataStream 作业：数据源 -> 清洗拆词 -> 按单词分组 -> 求和 -> 输出。
 */
public final class WordCountJob {

    private static final String DEFAULT_TEXT = "hello flink hello scaffold";

    private WordCountJob() {
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> counts = environment
                .fromData(Arrays.asList(resolveText(args).split("\\R")))
                .flatMap(new Tokenizer())
                .name("tokenize")
                .keyBy(wordCount -> wordCount.f0)
                .sum(1)
                .name("count");

        // Print sink 的结果可在 TaskManager 日志中查看。
        counts.print().name("stdout");
        environment.execute("scaffold-word-count");
    }

    static String resolveText(String[] args) {
        for (int index = 0; index < args.length; index++) {
            if ("--text".equals(args[index]) && index + 1 < args.length) {
                return args[index + 1];
            }
        }
        return DEFAULT_TEXT;
    }

    /** 将每行文本规范化为 (word, 1)。 */
    static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> output) {
            for (String word : line.toLowerCase(Locale.ROOT).split("\\W+")) {
                if (!word.isBlank()) {
                    output.collect(Tuple2.of(word, 1));
                }
            }
        }
    }
}
