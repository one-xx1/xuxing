import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 命令发送器，负责向服务端发送鼠标和键盘命令
 */
public class CommandSender {
    private ObjectOutputStream oos;
    
    public CommandSender(ObjectOutputStream oos) {
        this.oos = oos;
    }
    
    /**
     * 发送鼠标移动命令
     */
    public void sendMouseMove(int x, int y) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_MOUSE_MOVE);
            oos.writeObject(new MouseEventData(x, y, 0));
            oos.flush();
        }
    }
    
    /**
     * 发送鼠标按下命令
     */
    public void sendMousePress(int x, int y, int button) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_MOUSE_PRESS);
            oos.writeObject(new MouseEventData(x, y, button));
            oos.flush();
        }
    }
    
    /**
     * 发送鼠标释放命令
     */
    public void sendMouseRelease(int x, int y, int button) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_MOUSE_RELEASE);
            oos.writeObject(new MouseEventData(x, y, button));
            oos.flush();
        }
    }
    
    /**
     * 发送鼠标滚轮命令
     */
    public void sendMouseWheel(int x, int y, int wheelRotation) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_MOUSE_WHEEL);
            oos.writeObject(new MouseEventData(x, y, 0, wheelRotation));
            oos.flush();
        }
    }
    
    /**
     * 发送键盘按下命令
     */
    public void sendKeyPress(int keyCode, char keyChar) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_KEY_PRESS);
            oos.writeObject(new KeyEventData(keyCode, keyChar));
            oos.flush();
        }
    }
    
    /**
     * 发送键盘释放命令
     */
    public void sendKeyRelease(int keyCode, char keyChar) throws IOException {
        synchronized (oos) {
            oos.writeInt(Protocol.CMD_KEY_RELEASE);
            oos.writeObject(new KeyEventData(keyCode, keyChar));
            oos.flush();
        }
    }
    
    /**
     * 将Java KeyEvent的按钮代码转换为协议按钮代码
     */
    public static int convertButton(int javaButton) {
        if (javaButton == java.awt.event.MouseEvent.BUTTON1) return 1;
        if (javaButton == java.awt.event.MouseEvent.BUTTON2) return 2;
        if (javaButton == java.awt.event.MouseEvent.BUTTON3) return 3;
        return 1;
    }
}

