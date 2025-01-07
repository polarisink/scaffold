import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import static com.github.dockerjava.core.DefaultDockerClientConfig.*;

public class DockerClientExample {

    public static void main(String[] args) throws IOException {

        DockerClient dockerClient = connect();
        List<Container> exec = dockerClient.listContainersCmd().exec();
        System.out.println(exec.toString());
    }


    /**
     * 连接docker服务器
     *
     * @return
     */
    public static DockerClient connect() {
        // 配置docker CLI的一些选项
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerTlsVerify(DOCKER_TLS_VERIFY)
                .withDockerHost("localhost")
                // 与docker版本对应，参考https://docs.docker.com/engine/api/#api-version-matrix
                // 或者通过docker version指令查看api version
                .withApiVersion(API_VERSION)
                .withRegistryUrl(REGISTRY_URL)
                .build();

        URI dockerHost = config.getDockerHost();
        // 创建DockerHttpClient
        DockerHttpClient httpClient = new ApacheDockerHttpClient
                .Builder()
                .dockerHost(dockerHost)
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        Info info = dockerClient.infoCmd().exec();
        System.out.println(info.toString());
        return dockerClient;
    }
}