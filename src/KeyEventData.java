import java.io.Serializable;

/**
 * 键盘事件数据类
 */
public class KeyEventData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int keyCode;
    private char keyChar;
    
    public KeyEventData(int keyCode, char keyChar) {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }
    
    public int getKeyCode() {
        return keyCode;
    }
    
    public char getKeyChar() {
        return keyChar;
    }
}






