package github.polarisink.service;

/**
 * @author lqs
 * @date 2022/3/18
 */
public interface IFile {
    void upload(String fileName);

    void upload(String fileName,String object);
}
