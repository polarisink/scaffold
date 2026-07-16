package com.scaffold;

import com.corundumstudio.socketio.SocketIOServer;
import com.scaffold.socket.util.WsManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = WebSocketApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "scaffold.websocket.port=0")
class WebSocketStarterIntegrationTest {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private WsManager wsManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int webPort;

    @Test
    void applicationStartsWithWebSocketStarter() {
        assertThat(socketIOServer).isNotNull();
        assertThat(wsManager).isNotNull();
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
}
