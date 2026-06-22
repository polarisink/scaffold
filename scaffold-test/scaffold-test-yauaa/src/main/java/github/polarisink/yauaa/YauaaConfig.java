package github.polarisink.yauaa;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YauaaConfig {
    @Bean
   public UserAgentAnalyzer userAgentAnalyzer() {
        return UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000).build();
    }
}
