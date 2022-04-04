package github.polarisink.service;

/**
 * @author lqs
 * @date 2022/3/18
 */
public interface IFileService extends IFile {

	void upload(String fileName, String object, String bucket);
}
