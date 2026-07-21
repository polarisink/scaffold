# scaffold-biz Native 构建脚本

本目录统一管理 `scaffold-biz` 的 GraalVM Native Image 构建脚本。Dockerfile 保留在模块根目录，因为 Docker 会按 Dockerfile 名称自动使用对应的 `.dockerignore`。

## 脚本用途

| 脚本 | 运行环境 | 用途 | 产物 |
| --- | --- | --- | --- |
| `build-native.sh` | macOS、Linux | 构建当前系统二进制，或通过 Docker 构建 Linux ELF | `target/scaffold-biz` 或 `target/native-linux/scaffold-biz` |
| `build-native-docker.sh` | 安装了 Docker 的系统 | 将 Linux ELF 封装成原生 Docker 镜像 | `scaffold-biz:native` |
| `build-native.bat` | Windows | 使用 Windows GraalVM 和 MSVC 构建 `.exe` | `target\scaffold-biz.exe` |

## macOS 或 Linux 本机二进制

```bash
./scaffold-biz/scripts/build-native.sh
```

等价于：

```bash
./mvnw -Pnative -pl scaffold-biz -am -Dmaven.test.skip=true clean package
```

macOS 生成 Mach-O，Linux 生成 ELF；二进制只能在相同操作系统和兼容 CPU 架构上运行。

## Linux 二进制

在 macOS 上构建供 Docker 使用的 Linux ELF：

```bash
./scaffold-biz/scripts/build-native.sh linux
```

该命令使用 `Dockerfile.native` 的 `native-export` 阶段构建并导出：

```text
scaffold-biz/target/native-linux/scaffold-biz
```

## Docker 原生镜像

```bash
./scaffold-biz/scripts/build-native-docker.sh
```

默认镜像名为 `scaffold-biz:native`。指定镜像名：

```bash
./scaffold-biz/scripts/build-native-docker.sh example/scaffold-biz:1.0
```

执行逻辑：

1. 检查 Docker 是否可用。
2. 如果 Linux ELF 不存在，自动调用 `build-native.sh linux`。
3. 验证已有二进制是 Linux ELF，避免误封装 macOS Mach-O。
4. 使用 `Dockerfile.native-binary` 封装运行时镜像，不重复执行 Native 编译。

运行示例：

```bash
docker run --rm -p 8082:8082 scaffold-biz:native
```

本地文件默认写入容器 `/app/storage`。生产环境建议挂载持久卷：

```bash
docker run --rm -p 8082:8082 \
  -v scaffold-biz-storage:/app/storage \
  scaffold-biz:native
```

## Windows 二进制

请在安装了 GraalVM JDK 21、Visual Studio Build Tools、C++ 工具链和 Windows SDK 的 Windows 环境运行：

```bat
scaffold-biz\scripts\build-native.bat
```

Windows `.exe` 是 PE 格式，不能放入 Debian/Linux Docker 镜像运行。如需 Windows 容器，必须使用 Windows Containers 和匹配宿主版本的 Windows 基础镜像。

## 平台关系

| 构建产物 | 可运行环境 |
| --- | --- |
| macOS Mach-O | macOS 本机 |
| Linux ELF | Linux 本机、Linux Docker 镜像 |
| Windows PE `.exe` | Windows 本机、Windows Container |
