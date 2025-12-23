/**
 * 通信协议常量定义
 */
public class Protocol {
    // 命令类型
    public static final int CMD_MOUSE_MOVE = 1;
    public static final int CMD_MOUSE_PRESS = 2;
    public static final int CMD_MOUSE_RELEASE = 3;
    public static final int CMD_MOUSE_WHEEL = 4;
    public static final int CMD_KEY_PRESS = 5;
    public static final int CMD_KEY_RELEASE = 6;
    
    // 默认端口
    public static final int DEFAULT_PORT = 8888;
    
    // 图像质量（0.0-1.0）
    public static final float IMAGE_QUALITY = 0.99f;
    
    // 帧率控制（毫秒）
    public static final int FRAME_INTERVAL = 30; // 30 FPS
}






