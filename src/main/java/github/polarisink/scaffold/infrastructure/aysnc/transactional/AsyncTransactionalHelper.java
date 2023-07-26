package github.polarisink.scaffold.infrastructure.aysnc.transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see <a href="http://www.jackzhang.cn/spring-data-jpa-guide/SpringDataJpa%E9%AB%98%E7%BA%A7%E7%94%A8%E6%B3%95%E8%A1%A5%E5%85%85/SpringDataJPA%E7%9A%84%E4%B8%80%E4%BA%9B%E9%AB%98%E7%BA%A7%E7%94%A8%E6%B3%95%E6%89%A9%E5%B1%95.html">异步事务</a>
 */
@Component
public class AsyncTransactionalHelper {
    private final TransactionalHelper transactionalHelper;

    public AsyncTransactionalHelper(TransactionalHelper transactionalHelper) {
        this.transactionalHelper = transactionalHelper;
    }

    @Async
    public <T, R> R asyncExecuteTransaction(Function<T, R> function, T t) {
        return transactionalHelper.transactional(function, t);
    }

    @Async
    public <R> R asyncExecuteTransaction(Supplier<R> supplier) {
        return transactionalHelper.transactional(supplier);
    }

    @Async
    public <T> void asyncExecuteTransaction(Consumer<T> consumer, T t) {
        transactionalHelper.transactional(consumer, t);
    }

   /*
   //异步方法使用事务解决方法
    @Autowired
    private AsyncTransactionalHelper asyncTransactionalHelper;
    private void expireTokenAndSendLogoutMessage(DecodedJWT jwt, SessionExtraParameterDTO logoutExtraParameterDTO) {
        UserType userType = UserType.valueOf(jwt.getClaim(Constant.JWT_PAYLOAD_USER_TYPE).asString().toUpperCase());
        asyncTransactionalHelper.asyncExecuteTransaction(() -> {
            LoginService loginStrategy = loginService.getGenericLoginService(userType);
            loginStrategy.clearUserSessions(jwt.getId(), DeactivationReason.LOGOUT);
            return null;
        });
    }

    //同步方法使用事务使用解决方法:
    @Autowired
    private TransactionalHelper transactionalHelper;
    MessageRequest result = transactionalHelper.transactional(() -> {
        MessageRequest value = new MessageRequest();
        value.setUuid(UUID.randomUUID().toString());
        log.info("MessageRequest is save :{}", value);
        getDefaultRepository().save(value);
        return messageRequest;
    });*/
}