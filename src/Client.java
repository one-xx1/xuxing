import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * 远程桌面客户端
 */
public class Client {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ScreenDisplay screenDisplay;
    private CommandSender commandSender;
    private boolean running = false;
    
    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();  // 强制发送 Header
        ois = new ObjectInputStream(socket.getInputStream());
        
        // 接收屏幕尺寸
        int width = ois.readInt();
        int height = ois.readInt();
        
        System.out.println("连接到服务器: " + host + ":" + port);
        System.out.println("屏幕尺寸: " + width + "x" + height);
        
        // 创建显示窗口
        screenDisplay = new ScreenDisplay(width, height);
        commandSender = new CommandSender(oos);
        
        // 设置事件监听
        setupEventListeners();
    }
    
    /**
     * 设置事件监听器
     */
    private void setupEventListeners() {
        ScreenDisplay.ScreenPanel screenPanel = screenDisplay.getScreenPanel();
        
        // 鼠标移动事件
        screenPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                sendMouseEvent(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                sendMouseEvent(e);
            }
            
            private void sendMouseEvent(MouseEvent e) {
                try {
                    Point screenPoint = screenDisplay.convertToScreenCoordinates(e.getPoint());
                    commandSender.sendMouseMove(screenPoint.x, screenPoint.y);
                } catch (IOException ex) {
                    System.err.println("发送鼠标移动命令失败: " + ex.getMessage());
                }
            }
        });
        
        // 鼠标点击事件
        screenPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    screenPanel.requestFocusInWindow();
                    Point screenPoint = screenDisplay.convertToScreenCoordinates(e.getPoint());
                    int button = CommandSender.convertButton(e.getButton());
                    commandSender.sendMousePress(screenPoint.x, screenPoint.y, button);
                } catch (IOException ex) {
                    System.err.println("发送鼠标按下命令失败: " + ex.getMessage());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    Point screenPoint = screenDisplay.convertToScreenCoordinates(e.getPoint());
                    int button = CommandSender.convertButton(e.getButton());
                    commandSender.sendMouseRelease(screenPoint.x, screenPoint.y, button);
                } catch (IOException ex) {
                    System.err.println("发送鼠标释放命令失败: " + ex.getMessage());
                }
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                try {
                    Point screenPoint = screenDisplay.convertToScreenCoordinates(e.getPoint());
                    commandSender.sendMouseWheel(screenPoint.x, screenPoint.y, e.getWheelRotation());
                } catch (IOException ex) {
                    System.err.println("发送鼠标滚轮命令失败: " + ex.getMessage());
                }
            }
        });
        
        // 键盘事件
        screenPanel.setFocusable(true);
        screenPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    commandSender.sendKeyPress(e.getKeyCode(), e.getKeyChar());
                } catch (IOException ex) {
                    System.err.println("发送键盘按下命令失败: " + ex.getMessage());
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    commandSender.sendKeyRelease(e.getKeyCode(), e.getKeyChar());
                } catch (IOException ex) {
                    System.err.println("发送键盘释放命令失败: " + ex.getMessage());
                }
            }
        });
        
        // 窗口关闭事件
        screenDisplay.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }
    
    /**
     * 启动客户端
     */
    public void start() {
        running = true;
        screenDisplay.setVisible(true);
        SwingUtilities.invokeLater(() -> screenDisplay.getScreenPanel().requestFocusInWindow());
        
        // 启动接收屏幕数据的线程
        Thread receiveThread = new Thread(this::receiveScreenLoop);
        receiveThread.start();
    }
    
    /**
     * 循环接收屏幕数据
     */
    private void receiveScreenLoop() {
        try {
            while (running && !socket.isClosed()) {
                // 读取图像数据长度
                int dataLength = ois.readInt();
                
                // 读取图像数据
                byte[] imageData = new byte[dataLength];
                int totalRead = 0;
                while (totalRead < dataLength) {
                    int bytesRead = ois.read(imageData, totalRead, dataLength - totalRead);
                    if (bytesRead == -1) {
                        throw new IOException("连接已关闭");
                    }
                    totalRead += bytesRead;
                }
                
                // 更新显示
                SwingUtilities.invokeLater(() -> screenDisplay.updateImage(imageData));
            }
        } catch (Exception e) {
            if (running) {
                System.err.println("接收屏幕数据时出错: " + e.getMessage());
                JOptionPane.showMessageDialog(screenDisplay, "连接已断开: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            close();
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
        if (screenDisplay != null) {
            screenDisplay.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String host = "192.168.1.36";  // 默认IP
                int port = Protocol.DEFAULT_PORT; // 默认端口

                if (args.length > 0) {
                    // 输入格式：ip:port
                    if (args[0].contains(":")) {
                        String[] parts = args[0].split(":");
                        host = parts[0];
                        port = Integer.parseInt(parts[1]);
                    } else {
                        // 输入只有 IP，没有端口
                        host = args[0];
                    }
                }

                System.out.println("连接到服务器: " + host + ":" + port);

                Client client = new Client(host, port);
                client.start();

            } catch (Exception e) {
                System.err.println("客户端启动失败: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "连接失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}

