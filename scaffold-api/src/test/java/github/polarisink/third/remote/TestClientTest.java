package github.polarisink.third.remote;

import github.polarisink.BaseJunit4;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author aries
 * @date 2022/12/28
 */
public class TestClientTest extends BaseJunit4 {

    @Autowired
    TestClient testClient;

    @Test
    public void ping() {
        System.out.println(testClient.ping());
    }
}