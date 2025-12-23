import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 屏幕显示窗口
 */
public class ScreenDisplay extends JFrame {
    private ScreenPanel screenPanel;
    private int screenWidth;
    private int screenHeight;
    
    public ScreenDisplay(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        setTitle("远程桌面 - 客户端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        screenPanel = new ScreenPanel();
        add(screenPanel, BorderLayout.CENTER);
        
        // 计算窗口大小，适配屏幕但留出边距
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = screenSize.width - 100;
        int maxHeight = screenSize.height - 100;
        
        // 计算缩放比例，保持宽高比
        double scaleX = (double) maxWidth / width;
        double scaleY = (double) maxHeight / height;
        double scale = Math.min(scaleX, scaleY);
        
        int windowWidth = (int) (width * scale);
        int windowHeight = (int) (height * scale);
        
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
    }
    
    /**
     * 更新显示的图像
     * @param imageData 图像字节数据
     */
    public void updateImage(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image != null) {
                screenPanel.setImage(image);
            }
        } catch (IOException e) {
            System.err.println("更新图像时出错: " + e.getMessage());
        }
    }
    
    /**
     * 将面板坐标转换为屏幕坐标
     */
    public Point convertToScreenCoordinates(Point panelPoint) {
        return screenPanel.convertToScreenCoordinates(panelPoint);
    }
    
    /**
     * 获取屏幕面板（用于鼠标事件）
     */
    public ScreenPanel getScreenPanel() {
        return screenPanel;
    }
    
    public int getScreenWidth() {
        return screenWidth;
    }
    
    public int getScreenHeight() {
        return screenHeight;
    }
    
    /**
     * 自定义面板，用于绘制和缩放图像
     */
    public class ScreenPanel extends JPanel {
        private BufferedImage image;
        private int imageX, imageY, imageWidth, imageHeight;
        
        public ScreenPanel() {
            setBackground(Color.BLACK);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
        }
        
        public void setImage(BufferedImage image) {
            this.image = image;
            calculateImageBounds();
            repaint();
        }
        
        private void calculateImageBounds() {
            if (image == null) return;
            
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            if (panelWidth == 0 || panelHeight == 0) {
                // 如果面板还没有大小，使用首选尺寸
                Dimension size = getPreferredSize();
                if (size != null) {
                    panelWidth = size.width;
                    panelHeight = size.height;
                }
            }
            
            // 计算缩放比例，保持宽高比
            double scaleX = (double) panelWidth / image.getWidth();
            double scaleY = (double) panelHeight / image.getHeight();
            double scale = Math.min(scaleX, scaleY);
            
            // 计算实际显示的图像尺寸
            imageWidth = (int) (image.getWidth() * scale);
            imageHeight = (int) (image.getHeight() * scale);
            
            // 居中显示
            imageX = (panelWidth - imageWidth) / 2;
            imageY = (panelHeight - imageHeight) / 2;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                calculateImageBounds();
                g.drawImage(image, imageX, imageY, imageWidth, imageHeight, this);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            if (image != null) {
                return new Dimension(image.getWidth(), image.getHeight());
            }
            return new Dimension(screenWidth, screenHeight);
        }
        
        /**
         * 将面板坐标转换为屏幕坐标
         */
        public Point convertToScreenCoordinates(Point panelPoint) {
            if (image == null) {
                return new Point(0, 0);
            }
            
            // 计算相对于图像显示区域的坐标
            int relativeX = panelPoint.x - imageX;
            int relativeY = panelPoint.y - imageY;
            
            // 检查是否在图像显示区域内
            if (relativeX < 0 || relativeX >= imageWidth || 
                relativeY < 0 || relativeY >= imageHeight) {
                return new Point(0, 0);
            }
            
            // 计算缩放比例
            double scaleX = (double) image.getWidth() / imageWidth;
            double scaleY = (double) image.getHeight() / imageHeight;
            
            // 转换为原始屏幕坐标
            int screenX = (int) (relativeX * scaleX);
            int screenY = (int) (relativeY * scaleY);
            
            // 确保坐标在有效范围内
            screenX = Math.max(0, Math.min(screenX, screenWidth - 1));
            screenY = Math.max(0, Math.min(screenY, screenHeight - 1));
            
            return new Point(screenX, screenY);
        }
    }
}

