package jdk;

import org.junit.Test;

/**
 * @author aries
 * @date 2022/9/27
 */
public class ReturnTest {

    @Test
    public void context() {
        me();
        System.out.println("main end");
    }

    public void me() {
        for (int i = 0; i < 10; i++) {
            if (i > 5) {
                //直接退出me函数
                return;
            }
            System.out.println(i);
        }
        System.out.println("me end");
    }
}
