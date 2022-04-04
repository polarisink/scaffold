package github.polarisink.service;

import org.springframework.stereotype.Service;

/**
 * @author lqs
 * @date 2022/3/18
 */
@Service
public class FileService/* implements IFileService*/ {

   /* private final Logger log = LoggerFactory.getLogger(getClass());

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public FileService(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    private String getDefaultBucketName() {
        return minioConfig.getBucketName();
    }

    @Override
    public void upload(String fileName, String object, String bucket) {
        uploadObject(fileName, object, bucket);
    }

    @Override
    public void upload(String fileName) {
        uploadObject(fileName, null, getDefaultBucketName());
    }

    @Override
    public void upload(String fileName, String object) {
        uploadObject(fileName, object, getDefaultBucketName());
    }

    *//**
	 * 上传
	 *
	 * @param filename
	 * @param object
	 * @param bucket
	 *//*
    private void uploadObject(String filename, String object, String bucket) {
        if (StringUtils.isAnyBlank(filename, bucket))
            return;
        try {
            //存储桶构建
            bucketBuild(bucket);
            //保存的文件名称
            object = StringUtils.isBlank(object) ? filename.substring(filename.lastIndexOf("/") > 0 ? filename.lastIndexOf("/") : filename.lastIndexOf("\\")) : object;

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .filename(filename)
                            .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException exception) {
            log.error("uploadObject error", exception);
        }
    }


    *//**
	 * 存储桶构建
	 *
	 * @param bucketName
	 *//*
    private void bucketBuild(String bucketName) {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket " + bucketName + " make success.");
            } else {
                log.info("Bucket " + bucketName + " already exists.");
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException exception) {
            log.error("bucketBuild error", exception);
        }
    }
*/
}
