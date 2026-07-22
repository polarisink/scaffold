# scaffold-test-native 构建脚本

| 脚本 | 用途 | 产物 |
| --- | --- | --- |
| `build-native.sh` | 构建当前系统二进制 | `target/scaffold-test-native` |
| `build-native.sh linux` | 使用 Docker 构建 Linux ELF | `target/native-linux/scaffold-test-native` |
| `build-native-docker.sh` | 自动准备 Linux ELF 并封装镜像 | `scaffold-test-native:native` |
| `build-native.bat` | 在 Windows + GraalVM 环境构建 EXE | `target\scaffold-test-native.exe` |

## macOS/Linux 本机打包

```bash
./scaffold-test/scaffold-test-native/scripts/build-native.sh
```

## Docker 原生镜像

```bash
./scaffold-test/scaffold-test-native/scripts/build-native-docker.sh
```

如果 Linux 二进制不存在，脚本会先通过 Docker 自动构建。可指定镜像名：

```bash
./scaffold-test/scaffold-test-native/scripts/build-native-docker.sh example/scaffold-test-native:1.0
```

运行：

```bash
docker run --rm -p 8082:8082 scaffold-test-native:native
```

## Windows

在安装 GraalVM JDK 21、Visual Studio Build Tools 和 Windows SDK 的终端中运行：

```bat
scaffold-test\scaffold-test-native\scripts\build-native.bat
```

Windows EXE 不能直接放入 Linux Docker 镜像运行。
