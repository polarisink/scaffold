package rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static rocketmq.Common.ROCKETMQ_URL;

public class TransactionProducer {
  public static void main(String[] args) throws MQClientException, InterruptedException {
    TransactionListener transactionListener = new TransactionListenerImpl();
    TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");
    ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), r -> {
      Thread thread = new Thread(r);
      thread.setName("client-transaction-msg-check-thread");
      return thread;
    });
    producer.setNamesrvAddr(ROCKETMQ_URL);
    producer.setExecutorService(executorService);
    producer.setTransactionListener(transactionListener);
    producer.start();

    String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
    for (int i = 0; i < 10; i++) {
      try {
        Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i, ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = producer.sendMessageInTransaction(msg, null);
        System.out.printf("%s%n", sendResult);
        Thread.sleep(10);
      } catch (MQClientException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    for (int i = 0; i < 100000; i++) {
      Thread.sleep(1000);
    }
    producer.shutdown();
  }

  static class TransactionListenerImpl implements TransactionListener {
    private final AtomicInteger transactionIndex = new AtomicInteger(0);

    private final ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
      int value = transactionIndex.getAndIncrement();
      int status = value % 3;
      localTrans.put(msg.getTransactionId(), status);
      return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
      Integer status = localTrans.get(msg.getTransactionId());
      if (null != status) {
        switch (status) {
          case 0:
            return LocalTransactionState.UNKNOW;
            case 2:
            return LocalTransactionState.ROLLBACK_MESSAGE;
            case 1:
            default:
            return LocalTransactionState.COMMIT_MESSAGE;
        }
      }
      return LocalTransactionState.COMMIT_MESSAGE;
    }
  }
}