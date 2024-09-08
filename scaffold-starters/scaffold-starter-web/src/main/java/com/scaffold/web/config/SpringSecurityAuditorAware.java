package com.scaffold.web.config;

import com.scaffold.security.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author lqsgo
 */
@Slf4j
@Configuration
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(LoginUser.userId());
    }
}
