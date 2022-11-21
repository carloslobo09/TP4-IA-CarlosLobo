
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utils {

    private Utils() {
    }

    /*
	 * Convierte una imagen dada en una imagen en escala de grises
     */
    public static BufferedImage toGray(BufferedImage img) {
        BufferedImage grayImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = grayImage.createGraphics();
        try {
            graphics.setComposite(AlphaComposite.Src);
            graphics.drawImage(img, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return grayImage;
    }

    /*
	 * Escribe en el disco la imagen dada
     */
    public static void writeImage(BufferedImage image, String path) throws IOException {
        File output = new File(path);
        ImageIO.write(image, "png", output);
    }

    /*
	 * Reescalar píxel a píxel los datos de un factor igual a scaleFact
     */
    public static BufferedImage rescaleData(float scaleFact, BufferedImage image) {
        RescaleOp rOp = new RescaleOp(scaleFact, 0, null);
        image = rOp.filter(image, image);
        return image;
    }
}
