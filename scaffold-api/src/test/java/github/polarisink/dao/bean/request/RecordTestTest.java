package github.polarisink.dao.bean.request;


import org.junit.Test;

/**
 * @author lqs
 * @date 2022/12/24
 */
public class RecordTestTest {

    @Test
    public void testRecord() {
        RecordTest record = new RecordTest("lqs", 23, false);
        System.out.println(record);
    }
}