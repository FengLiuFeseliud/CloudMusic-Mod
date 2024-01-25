package fengliu.cloudmusic.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PNGConverter {

    public static ByteArrayInputStream convertJPEGtoPNG(InputStream jpegInputStream) {
        try {
            BufferedImage image = ImageIO.read(jpegInputStream);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", pngOutputStream);
            ByteArrayInputStream pngInputStream = new ByteArrayInputStream(pngOutputStream.toByteArray());

            pngOutputStream.close();
            jpegInputStream.close();

            return pngInputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}