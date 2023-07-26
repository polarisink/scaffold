package github.polarisink.scaffold.infrastructure.aysnc.executors;

import java.util.concurrent.ThreadFactory;


/**
 * @author hzsk
 */
public class MyThreadFactory implements ThreadFactory {

    /**
     * 该方法用来创建线程
     *
     * @param r
     * @return
     */
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
