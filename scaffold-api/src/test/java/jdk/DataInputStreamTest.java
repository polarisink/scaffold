package jdk;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataInputStreamTest {

    @Test
    public void con() {
        Path path = Paths.get("C:", "Users", "lqsgo", "Desktop", "test");
        writeData2file(path, "12347");
        writeData2file(path, "asdfgh");
        writeData2file(path, "754321");
    }

    private void writeData2file(Path locate, String data) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(locate.toFile(),
                true); BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                fileOutputStream); DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream)) {
            dataOutputStream.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
