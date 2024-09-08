package com.scaffold.biz.asyc;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CompletableFutureTest {

    @Test
    void accept() {
        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<String> result2 = new AtomicReference<>();
        AtomicReference<String> result3 = new AtomicReference<>();
        AtomicReference<String> result4 = new AtomicReference<>();
        CompletableFuture.allOf(
                getFuture(1).thenAccept(result::set),
                getFuture(2).thenAccept(result2::set),
                getFuture(3).thenAccept(result3::set),
                getFuture(4).thenAccept(result4::set)
        ).join();
        System.out.println(List.of(result.get(), result2.get(), result3.get(), result4.get()));
    }

    @Test
    void thenCombine() {
        CompletableFuture<String> future = getFuture(0);
        CompletableFuture<String> future1 = getFuture(1);
        CompletableFuture<String> future2 = getFuture(2);
        CompletableFuture<String> future3 = getFuture(3);

        String join = future
                .thenCombine(future1, (f1, f2) -> f1 + f2)
                .thenCombine(future2, (f1, f2) -> f1 + f2)
                .thenCombine(future3, (f1, f2) -> f1 + f2).join();
        System.out.println(join);
    }

    public CompletableFuture<String> getFuture(int i) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                return String.valueOf(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
