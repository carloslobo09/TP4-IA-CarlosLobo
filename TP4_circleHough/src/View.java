
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class View extends JFrame {

    private static final long serialVersionUID = 1L;

    public View(ArrayList<BufferedImage> images, String dir) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setTitle("HT Processing Results");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        resize(images, 250, 250);

        JPanel interImages = new JPanel();
        interImages.setLayout(new GridLayout(2, 5, 1, 0));
        interImages.add(new JLabel(new ImageIcon(images.get(0))));
        interImages.add(new JLabel(new ImageIcon(images.get(1))));
        interImages.add(new JLabel(new ImageIcon(images.get(2))));
        interImages.add(new JLabel(new ImageIcon(images.get(3))));
        interImages.add(new JLabel(new ImageIcon(images.get(4))));
        JLabel lab1 = new JLabel("Origin image", SwingConstants.CENTER);
        lab1.setVerticalAlignment(SwingConstants.TOP);
        JLabel lab2 = new JLabel("Gray scale image", SwingConstants.CENTER);
        lab2.setVerticalAlignment(SwingConstants.TOP);
        JLabel lab3 = new JLabel("Blurred image", SwingConstants.CENTER);
        lab3.setVerticalAlignment(SwingConstants.TOP);
        JLabel lab4 = new JLabel("Sobel image", SwingConstants.CENTER);
        lab4.setVerticalAlignment(SwingConstants.TOP);
        JLabel lab5 = new JLabel("Sobel image with threshold", SwingConstants.CENTER);
        lab5.setVerticalAlignment(SwingConstants.TOP);

        interImages.add(lab1);
        interImages.add(lab2);
        interImages.add(lab3);
        interImages.add(lab4);
        interImages.add(lab5);

        JPanel resImg = new JPanel();
        resImg.setLayout(new GridLayout(2, 1, 1, 0));
        resImg.add(new JLabel(new ImageIcon(images.get(5))));
        JLabel lab6 = new JLabel("Hough Transform result", SwingConstants.CENTER);
        lab6.setVerticalAlignment(SwingConstants.TOP);
        resImg.add(lab6);

        getContentPane().add(resImg, BorderLayout.NORTH);
        getContentPane().add(interImages, BorderLayout.CENTER);
    }

    /*
	 * Cambie el tamaño de las imágenes para que se emitan a un ancho máximo de maxW o una altura máxima de maxH, las otras dimensiones se calculan en consecuencia
     */
    private static void resize(ArrayList<BufferedImage> images, int maxW, int maxH) {
        for (int counter = 0; counter < images.size(); counter++) {
            BufferedImage img = images.get(counter);

            if (img.getWidth() > img.getHeight()) {
                if (img.getWidth() > maxW) {
                    Image tmp = img.getScaledInstance(maxW, img.getWidth() / img.getHeight() * maxW, Image.SCALE_SMOOTH);
                    BufferedImage dimg = new BufferedImage(maxW, img.getWidth() / img.getHeight() * maxW, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2d = dimg.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();

                    images.set(counter, dimg);
                }
            } else {
                if (img.getHeight() > maxH) {
                    Image tmp = img.getScaledInstance(img.getHeight() / img.getWidth() * maxH, maxH, Image.SCALE_SMOOTH);
                    BufferedImage dimg = new BufferedImage(img.getHeight() / img.getWidth() * maxH, maxH, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2d = dimg.createGraphics();
                    g2d.drawImage(tmp, 0, 0, null);
                    g2d.dispose();

                    images.set(counter, dimg);
                }
            }
        }
    }
}
