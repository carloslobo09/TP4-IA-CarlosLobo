
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class HoughLine extends Line2D.Float implements Comparable<HoughLine> {

    protected double theta;
    protected double r;
    protected int score;

    /**
     * Inicializa la línea o recta
     */
    public HoughLine(double theta, double r, int width, int height, int score) {
        this.theta = theta;
        this.r = r;
        this.score = score;

        // Durante el procesamiento, h_h se duplica para que tenga valores r
        int houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

        // Encuentre puntos de borde y vote en matriz
        float centerX = width / 2;
        float centerY = height / 2;

        // Dibujar bordes en la matriz de salida
        double tsin = Math.sin(theta);
        double tcos = Math.cos(theta);

        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
            int x1 = 0, y1 = 0;
            int x2 = 0, y2 = height - 1;

            x1 = (int) ((((r - houghHeight) - ((y1 - centerY) * tsin)) / tcos) + centerX);
            x2 = (int) ((((r - houghHeight) - ((y2 - centerY) * tsin)) / tcos) + centerX);

            setLine(x1, y1, x2, y2);
        } else {
            int x1 = 0, y1 = 0;
            int x2 = width - 1, y2 = 0;

            y1 = (int) ((((r - houghHeight) - ((x1 - centerX) * tcos)) / tsin) + centerY);
            y2 = (int) ((((r - houghHeight) - ((x2 - centerX) * tcos)) / tsin) + centerY);

            setLine(x1, y1, x2, y2);
        }
    }

    public int compareTo(HoughLine o) {
        return (this.score - o.score);
    }
}
