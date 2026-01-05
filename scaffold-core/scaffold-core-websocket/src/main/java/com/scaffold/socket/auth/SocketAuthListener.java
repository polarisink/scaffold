package com.scaffold.socket.auth;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.HandshakeData;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 认证监听器
 *
 * @author machenike
 */
@Component
@RequiredArgsConstructor
@ConditionalOnMissingBean(AuthorizationListener.class)
public class SocketAuthListener implements AuthorizationListener {


    @Override
    public AuthorizationResult getAuthorizationResult(HandshakeData handshakeData) {
        return new AuthorizationResult(true);
    }
}
