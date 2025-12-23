import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * 服务器处理器，处理单个客户端连接
 */
public class ServerHandler extends Thread {
    private Socket socket;
    private ScreenCapture screenCapture;
    private ImageCompressor imageCompressor;
    private Robot robot;
    private boolean running = true;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    public ServerHandler(Socket socket, ScreenCapture screenCapture, ImageCompressor imageCompressor) throws IOException, AWTException {
        this.socket = socket;
        this.screenCapture = screenCapture;
        this.imageCompressor = imageCompressor;
        this.robot = new Robot(); // 创建 Robot 实例，用于模拟输入

        // 初始化输入输出流   先创建 ObjectOutputStream，再创建 ObjectInputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();  // 强制把 Header 推出去
        ois = new ObjectInputStream(socket.getInputStream());
        
        // 发送屏幕尺寸信息
        Dimension screenSize = screenCapture.getScreenSize();
        oos.writeInt(screenSize.width);
        oos.writeInt(screenSize.height);
        oos.flush();
    }
    
    @Override
    public void run() {
        // 启动屏幕传输线程     开一个子线程一直发屏幕
        Thread screenThread = new Thread(this::sendScreenLoop);
        screenThread.start();
        
        // 处理客户端命令     当前线程（ServerHandler 主线程）专门收命令
        handleCommands();
    }
    
    /**
     * 循环发送屏幕图像
     */
    private void sendScreenLoop() {
        try {
            while (running && !socket.isClosed()) {
                // 捕获屏幕
                BufferedImage image = screenCapture.captureScreen();
                
                // 压缩图像
                byte[] imageData = imageCompressor.compressImage(image);
                
                // 发送图像数据
                synchronized (oos) {
                    oos.writeInt(imageData.length);
                    oos.write(imageData);
                    oos.flush();
                }
                
                // 控制帧率
                Thread.sleep(Protocol.FRAME_INTERVAL); //FPS，每秒发送多少张高清截图
            }
        } catch (Exception e) {
            System.err.println("发送屏幕数据时出错: " + e.getMessage());
            running = false;
        }
        //每帧：截图 → 压缩 → 发送“长度+数据”
    }
    
    /**
     * 处理客户端命令
     */
    private void handleCommands() {
        try {
            while (running && !socket.isClosed()) {
                int command = ois.readInt();    // 阻塞等待客户端发命令
                
                switch (command) {
                    case Protocol.CMD_MOUSE_MOVE:
                        MouseEventData mouseMove = (MouseEventData) ois.readObject();
                        robot.mouseMove(mouseMove.getX(), mouseMove.getY());
                        break;
                        
                    case Protocol.CMD_MOUSE_PRESS:
                        MouseEventData mousePress = (MouseEventData) ois.readObject();
                        int button = mousePress.getButton();
                        int javaButton = convertButton(button);
                        robot.mousePress(javaButton);
                        break;
                        
                    case Protocol.CMD_MOUSE_RELEASE:
                        MouseEventData mouseRelease = (MouseEventData) ois.readObject();
                        int releaseButton = convertButton(mouseRelease.getButton());
                        robot.mouseRelease(releaseButton);
                        break;
                        
                    case Protocol.CMD_MOUSE_WHEEL:
                        MouseEventData mouseWheel = (MouseEventData) ois.readObject();
                        robot.mouseWheel(mouseWheel.getWheelRotation());
                        break;
                        
                    case Protocol.CMD_KEY_PRESS:
                        KeyEventData keyPress = (KeyEventData) ois.readObject();
                        robot.keyPress(keyPress.getKeyCode());
                        break;
                        
                    case Protocol.CMD_KEY_RELEASE:
                        KeyEventData keyRelease = (KeyEventData) ois.readObject();
                        robot.keyRelease(keyRelease.getKeyCode());
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("处理命令时出错: " + e.getMessage());
        } finally {
            close();    // running = false + 关闭所有流 + socket
        }
    }
    
    /**
     * 转换按钮代码（1=左键, 2=中键, 3=右键）
     */
    private int convertButton(int button) {
        switch (button) {
            case 1:
                return InputEvent.BUTTON1_MASK;
            case 2:
                return InputEvent.BUTTON2_MASK;
            case 3:
                return InputEvent.BUTTON3_MASK;
            default:
                return InputEvent.BUTTON1_MASK;
        }
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        running = false;
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

