import java.io.Serializable;

/**
 * 鼠标事件数据类
 */
public class MouseEventData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x;
    private int y;
    private int button; // 1=左键, 2=中键, 3=右键
    private int wheelRotation; // 滚轮滚动量
    
    public MouseEventData(int x, int y, int button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }
    
    public MouseEventData(int x, int y, int button, int wheelRotation) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.wheelRotation = wheelRotation;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getButton() {
        return button;
    }
    
    public int getWheelRotation() {
        return wheelRotation;
    }
}






