package github.polarisink.third.config;

import github.polarisink.third.remote.TestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author aries
 * @date 2022/12/28
 */
@Configuration
public class RemoteClientConfig {
    @Bean
    public TestClient demoApi() {
        WebClient client = WebClient.builder().baseUrl("http://localhost:8065/assembly").build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(TestClient.class);
    }
}
