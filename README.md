# 远程桌面共享系统

基于Java Socket实现的远程桌面共享系统，支持屏幕共享和远程控制（鼠标/键盘）。

## 功能特性

- ✅ **屏幕捕获与传输**：服务端使用Robot类捕获屏幕并压缩传输
- ✅ **图像压缩**：使用JPEG压缩减少网络传输量
- ✅ **远程控制**：支持鼠标移动、点击、滚轮和键盘输入
- ✅ **实时显示**：客户端实时显示远程屏幕画面
- ✅ **多客户端支持**：服务端可同时处理多个客户端连接

## 技术要点

- **Socket通信**：基于TCP的可靠数据传输
- **Robot类**：用于屏幕捕获和事件模拟
- **图像压缩**：JPEG格式压缩，可配置压缩质量
- **事件转发**：鼠标和键盘事件的序列化传输

## 系统要求

- JDK 8 或更高版本
- 支持AWT的图形环境

## 使用方法

### 启动服务端

```bash
# 使用默认端口8888
java -jar Server.jar

# 或指定端口
java -jar Server.jar 9999
```

### 启动客户端

```bash
# 连接到localhost的默认端口
java -jar Client.jar

# 或指定服务器地址和端口
java -jar Client.jar 192.168.1.100 8888
```

## 项目结构

```
src/
├── Protocol.java          # 通信协议常量定义
├── MouseEventData.java    # 鼠标事件数据类
├── KeyEventData.java      # 键盘事件数据类
├── ScreenCapture.java     # 屏幕捕获类（服务端）
├── ImageCompressor.java   # 图像压缩类（服务端）
├── Server.java            # 服务器主类
├── ServerHandler.java     # 服务器处理器（处理客户端连接）
├── Client.java            # 客户端主类
├── ScreenDisplay.java     # 屏幕显示窗口（客户端）
└── CommandSender.java     # 命令发送器（客户端）
```

## 通信协议

### 命令类型

- `CMD_MOUSE_MOVE (1)`: 鼠标移动
- `CMD_MOUSE_PRESS (2)`: 鼠标按下
- `CMD_MOUSE_RELEASE (3)`: 鼠标释放
- `CMD_MOUSE_WHEEL (4)`: 鼠标滚轮
- `CMD_KEY_PRESS (5)`: 键盘按下
- `CMD_KEY_RELEASE (6)`: 键盘释放

### 数据格式

1. **连接建立时**：服务端发送屏幕宽度和高度（两个int）
2. **屏幕传输**：图像数据长度（int）+ 图像字节数据
3. **命令传输**：命令类型（int）+ 事件数据对象（序列化）

## 配置参数

在 `Protocol.java` 中可以调整以下参数：

- `DEFAULT_PORT`: 默认端口号（8888）
- `IMAGE_QUALITY`: 图像压缩质量（0.0-1.0，默认0.99）
- `FRAME_INTERVAL`: 帧间隔（毫秒，默认33ms，即30 FPS）

## 注意事项

1. **权限要求**：在某些操作系统上，Robot类可能需要特殊权限
2. **网络延迟**：网络延迟会影响控制响应速度
3. **性能优化**：可以通过调整压缩质量和帧率来平衡画质和性能
4. **安全考虑**：本系统仅用于学习和演示，生产环境需要添加身份验证和加密

## 开发环境

- 开发工具：IntelliJ IDEA
- JDK版本：JDK 8
- 操作系统：Windows/Linux/macOS





