package github.polarisink.scaffold.infrastructure.aysnc.transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class TransactionalHelper {
    @Transactional(rollbackFor = Exception.class)
    public <T,R> R transactional(Function<T,R> function, T t){
        return function.apply(t);
    }
    @Transactional(rollbackFor = Exception.class)
    public <R> R transactional(Supplier<R> supplier){
        return supplier.get();
    }
    @Transactional(rollbackFor = Exception.class)
    public <T> void transactional(Consumer<T> consumer, T t){
        consumer.accept(t);
    }
}