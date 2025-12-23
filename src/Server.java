import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 远程桌面服务端
 */
public class Server {
    private ServerSocket serverSocket;
    private ScreenCapture screenCapture;
    private ImageCompressor imageCompressor;
    private boolean running = false;
    
    public Server(int port) throws Exception {
        serverSocket = new ServerSocket(port);                  // 监听 8888 端口
        screenCapture = new ScreenCapture();                    // 创建 Robot，准备截屏
        imageCompressor = new ImageCompressor();                // 准备 JPEG 压缩器
        System.out.println("服务器启动，监听端口: " + port);
    }
    
    public void start() {
        running = true;
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();        // 阻塞，直到有人连
                System.out.println("客户端连接: " + clientSocket.getRemoteSocketAddress());
                // 立刻为这个客户端单独创建一个“遥控处理器”
                // 为每个客户端创建独立的处理器
                ServerHandler handler = new ServerHandler(clientSocket, screenCapture, imageCompressor);
                handler.start();    // ← 启动线程
            } catch (Exception e) {
                if (running) {
                    System.err.println("接受客户端连接时出错: " + e.getMessage());
                }
            }
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : Protocol.DEFAULT_PORT;
            Server server = new Server(port);
            server.start();
        } catch (Exception e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}






