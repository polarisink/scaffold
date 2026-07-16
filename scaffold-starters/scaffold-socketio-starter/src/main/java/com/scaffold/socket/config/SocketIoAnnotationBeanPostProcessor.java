package com.scaffold.socket.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Registers annotated Socket.IO listener beans exactly once. */
final class SocketIoAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final SocketIOServer server;
    private final Set<String> registeredBeanNames = ConcurrentHashMap.newKeySet();

    SocketIoAnnotationBeanPostProcessor(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = ClassUtils.getUserClass(bean);
        if (hasSocketIoListener(beanClass) && registeredBeanNames.add(beanName)) {
            server.addListeners(bean, beanClass);
        }
        return bean;
    }

    private boolean hasSocketIoListener(Class<?> beanClass) {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(beanClass);
        for (Method method : methods) {
            if (AnnotatedElementUtils.hasAnnotation(method, OnConnect.class)
                    || AnnotatedElementUtils.hasAnnotation(method, OnDisconnect.class)
                    || AnnotatedElementUtils.hasAnnotation(method, OnEvent.class)) {
                return true;
            }
        }
        return false;
    }
}
