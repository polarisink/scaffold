package com.scaffold.socket.auth;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.HandshakeData;

/**
 * 认证监听器
 *
 * @author machenike
 */
public class SocketAuthListener implements AuthorizationListener {


    @Override
    public AuthorizationResult getAuthorizationResult(HandshakeData handshakeData) {
        return new AuthorizationResult(true);
    }
}
