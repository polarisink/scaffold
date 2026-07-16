package com.scaffold.socket.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SocketIoAnnotationBeanPostProcessorTest {

    @Test
    void registersAnnotatedBeanOnlyOnce() {
        SocketIOServer server = mock(SocketIOServer.class);
        SocketIoAnnotationBeanPostProcessor processor = new SocketIoAnnotationBeanPostProcessor(server);
        TestListener listener = new TestListener();

        processor.postProcessAfterInitialization(listener, "testListener");
        processor.postProcessAfterInitialization(listener, "testListener");

        verify(server, times(1)).addListeners(listener, TestListener.class);
    }

    static class TestListener {
        @OnConnect
        public void onConnect(SocketIOClient client) {
        }
    }
}
