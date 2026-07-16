package com.scaffold;

import com.corundumstudio.socketio.SocketIOServer;
import com.scaffold.socket.util.WsManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.ServerSocket;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = WebSocketApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketStarterIntegrationTest {

    private static final int SOCKET_IO_PORT = availablePort();

    @DynamicPropertySource
    static void socketIoProperties(DynamicPropertyRegistry registry) {
        registry.add("scaffold.socketio.port", () -> SOCKET_IO_PORT);
    }

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private WsManager wsManager;

    @Autowired
    private WsServer wsServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int webPort;

    @Test
    void applicationStartsWithWebSocketStarter() {
        assertThat(socketIOServer).isNotNull();
        assertThat(wsManager).isNotNull();

        Queue<?> connectListeners = (Queue<?>) ReflectionTestUtils.getField(
                socketIOServer.getNamespace(""), "connectListeners");
        Queue<?> disconnectListeners = (Queue<?>) ReflectionTestUtils.getField(
                socketIOServer.getNamespace(""), "disconnectListeners");
        assertThat(connectListeners).hasSize(1);
        assertThat(disconnectListeners).hasSize(1);
    }

    @Test
    void servesSocketIoTestPageFromWebPort() {
        ResponseEntity<String> response = restTemplate.getForEntity("/socket-test.html", String.class);

        assertThat(webPort).isPositive();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Socket.IO WebSocket 测试");
        assertThat(response.getBody()).contains("id=\"port\" type=\"number\" value=\"8101\"");
        assertThat(response.getBody()).contains("sha384-Gr6Lu2Ajx28mzwyVR8CFkULdCU7kMlZ9UthllibdOSo6qAiN+yXNHqtgdTvFXMT4");
        assertThat(response.getBody()).contains("id=\"listen\" type=\"text\" value=\"message\"");
    }

    @Test
    void invokesConnectListenerOnceForOneSocketIoConnection() throws Exception {
        int invocationCountBeforeConnect = wsServer.connectInvocationCount();
        CountDownLatch connected = new CountDownLatch(1);
        IO.Options options = IO.Options.builder()
                .setQuery("clientId=integration-client")
                .setTransports(new String[]{"websocket"})
                .setReconnection(false)
                .build();
        Socket client = IO.socket(URI.create("http://127.0.0.1:" + SOCKET_IO_PORT), options);
        client.on(Socket.EVENT_CONNECT, args -> connected.countDown());

        try {
            client.connect();
            assertThat(connected.await(3, TimeUnit.SECONDS)).isTrue();
            Thread.sleep(200);
            assertThat(wsServer.connectInvocationCount() - invocationCountBeforeConnect).isEqualTo(1);
        } finally {
            client.disconnect();
            client.close();
        }
    }

    private static int availablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to allocate Socket.IO test port", exception);
        }
    }
}
