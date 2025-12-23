import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * 图像压缩类
 */
public class ImageCompressor {
    private ImageWriter writer;
    private ImageWriteParam param;
    
    public ImageCompressor() {
        // 获取JPEG图像写入器
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (writers.hasNext()) {
            writer = writers.next();
            param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(Protocol.IMAGE_QUALITY);
            }
        }
    }
    
    /**
     * 压缩图像为字节数组
     * @param image 原始图像
     * @return 压缩后的字节数组
     * @throws IOException IO异常
     */
    public byte[] compressImage(BufferedImage image) throws IOException {
        if (writer == null) {
            throw new IOException("JPEG writer not available");
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        }
        return baos.toByteArray();
    }
    
    /**
     * 将BufferedImage转换为PNG格式的字节数组（备用方案）
     * @param image 原始图像
     * @return PNG格式的字节数组
     * @throws IOException IO异常
     */
    public byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}






