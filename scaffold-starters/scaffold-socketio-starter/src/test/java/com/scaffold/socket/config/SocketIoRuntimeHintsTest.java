package com.scaffold.socket.config;

import com.corundumstudio.socketio.protocol.AuthPacket;
import com.corundumstudio.socketio.protocol.ConnPacket;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class SocketIoRuntimeHintsTest {

    @Test
    void registersProtocolPacketGettersForNativeJacksonSerialization() throws NoSuchMethodException {
        RuntimeHints hints = new RuntimeHints();
        new SocketIoRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection()
                .onMethod(AuthPacket.class.getMethod("getSid"))
                .test(hints)).isTrue();
        assertThat(RuntimeHintsPredicates.reflection()
                .onMethod(ConnPacket.class.getMethod("getSid"))
                .test(hints)).isTrue();
    }
}
