
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import filters.GaussianBlur;
import filters.Sobel;
import houghTransform.Circle;
import houghTransform.CircleDetector;

public class Main {

    public static void main(String[] args) throws Exception {

        /* Variables/argumentos predeterminados */
        final String path = "./results/";
        int sobelThreshold = 150;
        int minRadius = 10;
        int maxRadius = 100;
        int circleThreshold = 1;

        /* Comprueba el argumento y la carpeta de resultados */
        if (args.length < 1 || args.length > 5) {
            System.out.println("Usage: Main imagePath [sobelThreshold] [minRadius] [maxRadius] [circleThreshold]");
            System.exit(1);
        }

        if (args.length > 2) {

            try {
                switch (args.length) {
                    case 5:
                        if (Integer.parseInt(args[4]) > 1) {
                            circleThreshold = Integer.parseInt(args[4]);
                        } else {
                            System.out.println("Negative circleThreshold input, keeping default value (1)");
                        }
                    case 4:
                        maxRadius = Integer.parseInt(args[3]);
                    case 3:
                        minRadius = Integer.parseInt(args[2]);
                    case 2:
                        if (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 255) {
                            sobelThreshold = Integer.parseInt(args[1]);
                        } else {
                            System.out.println("sobelThreshold out of range, keeping default value (150)");
                        }
                }
            } catch (NumberFormatException e) {
                System.out.println("Specified arguments must be integers");
                System.exit(1);
            }
        }

        if (maxRadius - minRadius <= 0) {
            System.out.println("Minimum and maximum radius are not valid");
            System.exit(1);
        }

        File resFolder = new File(path);
        if (!(resFolder.exists() && resFolder.isDirectory())) {
            try {
                resFolder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayList<BufferedImage> images = new ArrayList<>();

        /* Lee la imagen original */
        File origin = new File(args[0]);

        try {
            images.add(ImageIO.read(origin));
        } catch (IOException e) {
            System.out.println("Image not found");
            System.exit(1);
        }

        /* Convierte la imagen en escala de grises */
        BufferedImage gray = Utils.toGray(images.get(0));
        images.add(gray);
        Utils.writeImage(gray, path + "gray.png");

        /* Desenfoque gaussiano */
        BufferedImage blurred = GaussianBlur.blur(gray);
        for (int i = 0; i < 2; i++) {
            blurred = GaussianBlur.blur(blurred);
        }
        images.add(blurred);
        Utils.writeImage(blurred, path + "blur.png");

        /* Detección de bordes Sobel */
        Sobel sob = new Sobel(blurred);

        BufferedImage imgSobel = sob.getSobelImage();
        BufferedImage imgSobelThreshold = sob.thresholdImg(sobelThreshold);

        images.add(imgSobel);
        images.add(imgSobelThreshold);
        Utils.writeImage(imgSobel, path + "sobel.png");
        Utils.writeImage(imgSobelThreshold, path + "sobelThreshold.png");

        /* Detección de círculos */
        BufferedImage circlesImg = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_INT_ARGB);

        CircleDetector circleDetector = new CircleDetector(sobelThreshold, minRadius, maxRadius);
        List<Circle> detectedCircles = circleDetector.circleDetection(imgSobel, sob.getSobelValues());
        Collections.sort(detectedCircles, Collections.reverseOrder());

        circlesImg.createGraphics().drawImage(images.get(0), 0, 0, null);
        Graphics2D graph = circlesImg.createGraphics();
        graph.setColor(Color.RED);

        if (circleThreshold > detectedCircles.size()) {
            circleThreshold = detectedCircles.size();
            System.out.println("circleThreshold is too high, printing all the circles in the accumulator...");
        }

        for (int i = 0; i < circleThreshold; i++) {
            Circle circleToDraw = detectedCircles.get(i);
            double x = circleToDraw.getX() - circleToDraw.getR() * Math.cos(0 * Math.PI / 180);
            double y = circleToDraw.getY() - circleToDraw.getR() * Math.sin(90 * Math.PI / 180);
            graph.drawOval((int) x, (int) y, 2 * circleToDraw.getR(), 2 * circleToDraw.getR());
        }

        images.add(circlesImg);
        Utils.writeImage(circlesImg, path + "detectedCircles.png");

        View gui = new View(images, path);
        gui.setVisible(true);
    }
}
