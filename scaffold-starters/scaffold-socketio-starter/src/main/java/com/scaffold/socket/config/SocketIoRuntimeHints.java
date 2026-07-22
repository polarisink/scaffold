package com.scaffold.socket.config;

import com.corundumstudio.socketio.protocol.AuthPacket;
import com.corundumstudio.socketio.protocol.ConnPacket;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/** Native Image reflection metadata required by netty-socketio protocol serialization. */
public class SocketIoRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(AuthPacket.class, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(ConnPacket.class, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
