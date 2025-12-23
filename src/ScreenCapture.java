import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 屏幕捕获类，使用Robot类捕获屏幕
 */
public class ScreenCapture {
    private Robot robot;
    private Rectangle screenRect;
    
    public ScreenCapture() throws AWTException {
        this.robot = new Robot();
        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenRect = new Rectangle(0, 0, screenSize.width, screenSize.height);
    }
    
    /**
     * 捕获整个屏幕
     * @return 屏幕图像
     */
    public BufferedImage captureScreen() {
        return robot.createScreenCapture(screenRect);
    }
    
    /**
     * 捕获指定区域的屏幕
     * @param rect 捕获区域
     * @return 屏幕图像
     */
    public BufferedImage captureScreen(Rectangle rect) {
        return robot.createScreenCapture(rect);
    }
    
    /**
     * 获取屏幕尺寸
     * @return 屏幕尺寸
     */
    public Dimension getScreenSize() {
        return screenRect.getSize();
    }
}






