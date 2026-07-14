package com.scaffold.sse;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class SseConnectionManagerTest {

    @Test
    void sendsToEveryConnectionOwnedByTheUser() {
        TestManager manager = new TestManager();
        manager.connect("user-1", List.of("room-a"));
        manager.connect("user-1", List.of("room-b"));
        manager.connect("user-2", List.of("room-a"));

        SseSendResult result = manager.sendToUser("user-1", "notice", "hello");

        assertThat(result.accepted()).isTrue();
        assertThat(result.enqueuedConnections()).isEqualTo(2);
        await(() -> manager.emitters.get(0).sendCount() == 2 && manager.emitters.get(1).sendCount() == 2);
        assertThat(manager.emitters).extracting(RecordingEmitter::sendCount).containsExactly(2, 2, 1);
    }

    @Test
    void broadcastsOnlyToConnectionsInTheRoom() {
        TestManager manager = new TestManager();
        manager.connect("user-1", List.of("room-a", "room-b"));
        manager.connect("user-2", List.of("room-a"));
        manager.connect("user-3", List.of("room-c"));

        SseSendResult result = manager.sendToRoom("room-a", "chat", "hello room");

        assertThat(result.enqueuedConnections()).isEqualTo(2);
        await(() -> manager.emitters.get(0).sendCount() == 2 && manager.emitters.get(1).sendCount() == 2);
        assertThat(manager.emitters).extracting(RecordingEmitter::sendCount).containsExactly(2, 2, 1);
    }

    @Test
    void removesAllIndexesWhenConnectionCompletes() {
        TestManager manager = new TestManager();
        RecordingEmitter emitter = (RecordingEmitter) manager.connect("user-1", List.of("room-a"));

        emitter.simulateCompletion();

        assertThat(manager.onlineConnectionCount()).isZero();
        assertThat(manager.onlineUserCount()).isZero();
        assertThat(manager.sendToRoom("room-a", "chat", "ignored").enqueuedConnections()).isZero();
    }

    @Test
    void removesBrokenConnectionWithoutAffectingHealthyConnections() {
        TestManager manager = new TestManager();
        manager.connect("user-1", List.of("room-a"));
        manager.connect("user-2", List.of("room-a"));
        await(() -> manager.emitters.stream().allMatch(emitter -> emitter.sendCount() == 1));
        manager.emitters.getFirst().fail = true;

        SseSendResult result = manager.sendToRoom("room-a", "chat", "hello");

        assertThat(result.enqueuedConnections()).isEqualTo(2);
        await(() -> manager.onlineConnectionCount() == 1 && manager.onlineUserCount() == 1);
        assertThat(manager.onlineConnectionCount()).isEqualTo(1);
        assertThat(manager.onlineUserCount()).isEqualTo(1);
    }

    @Test
    void disconnectsSlowClientInsteadOfBlockingPublisherWhenQueueIsFull() {
        BlockingManager manager = new BlockingManager();
        manager.connect("slow-user", List.of("room-a"));
        manager.emitter.awaitSending();

        assertThat(manager.sendToUser("slow-user", "message", "one").enqueuedConnections()).isEqualTo(1);
        assertThat(manager.sendToUser("slow-user", "message", "two").enqueuedConnections()).isZero();
        assertThat(manager.onlineConnectionCount()).isZero();
        manager.emitter.release();
    }

    private static final class TestManager extends SseConnectionManager {
        private final List<RecordingEmitter> emitters = new ArrayList<>();

        private TestManager() {
            this(new InMemorySseConnectionRepository());
        }

        private TestManager(SseConnectionRepository repository) {
            this(repository, new SseLocalDispatcher(repository, 10));
        }

        private TestManager(SseConnectionRepository repository, SseLocalDispatcher dispatcher) {
            super(repository, dispatcher, new LocalSseMessageBroker(dispatcher), 10);
        }

        @Override
        protected SseEmitter createEmitter(long timeout) {
            RecordingEmitter emitter = new RecordingEmitter(timeout);
            emitters.add(emitter);
            return emitter;
        }
    }

    private static final class RecordingEmitter extends SseEmitter {
        private volatile int sendCount;
        private volatile boolean fail;
        private Runnable completionCallback;

        private RecordingEmitter(long timeout) {
            super(timeout);
        }

        @Override
        public synchronized void send(SseEventBuilder builder) throws IOException {
            if (fail) throw new IOException("client disconnected");
            sendCount++;
        }

        @Override
        public void onCompletion(Runnable callback) {
            completionCallback = callback;
        }

        void simulateCompletion() {
            completionCallback.run();
        }

        int sendCount() {
            return sendCount;
        }
    }

    private static final class BlockingManager extends SseConnectionManager {
        private BlockingEmitter emitter;

        private BlockingManager() {
            this(new InMemorySseConnectionRepository());
        }

        private BlockingManager(SseConnectionRepository repository) {
            this(repository, new SseLocalDispatcher(repository, 1));
        }

        private BlockingManager(SseConnectionRepository repository, SseLocalDispatcher dispatcher) {
            super(repository, dispatcher, new LocalSseMessageBroker(dispatcher), 1);
        }

        @Override
        protected SseEmitter createEmitter(long timeout) {
            emitter = new BlockingEmitter(timeout);
            return emitter;
        }
    }

    private static final class BlockingEmitter extends SseEmitter {
        private final CountDownLatch sending = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);

        private BlockingEmitter(long timeout) {
            super(timeout);
        }

        @Override
        public void send(SseEventBuilder builder) throws IOException {
            sending.countDown();
            try {
                release.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
            }
        }

        void awaitSending() {
            try {
                sending.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new AssertionError(ex);
            }
        }

        void release() {
            release.countDown();
        }
    }

    private static void await(java.util.function.BooleanSupplier condition) {
        long deadline = System.nanoTime() + 1_000_000_000L;
        while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
        assertThat(condition.getAsBoolean()).isTrue();
    }
}
