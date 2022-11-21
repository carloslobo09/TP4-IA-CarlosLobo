
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.Vector;
import java.io.File;

import java.util.Collections;
import processing.core.PImage;

public class HoughTransform extends Thread {
    // El tamaño maximo

    final int neighbourhoodSize = 4;

    // valores discretos de theta comprobaremos 
    final int maxTheta = 180;

    // Usando maxTheta, calcule el paso
    final double thetaStep = Math.PI / maxTheta;

    // el ancho y alto de la imagen
    protected int width, height;

    // la matriz de hough
    protected int[][] houghArray;

    // las coordenadas del centro de la imagen
    protected float centerX, centerY;

    // la altura de la matriz Hough
    protected int houghHeight;

    // duplicar la altura de la copa (permite números negativos) 
    protected int doubleHeight;

    // el número de puntos que se han añadido
    protected int numPoints;

    // caché de valores de seno y coseno para diferentes valores theta. Tiene una mejora significativa en el rendimiento. 
    private double[] sinCache;
    private double[] cosCache;

    public HoughTransform(PImage image) {
        initialise(image.width, image.height);
        addPoints((BufferedImage) image.getImage());
    }

    public HoughTransform(BufferedImage image) {
        initialise(image.getWidth(), image.getHeight());
        addPoints(image);
    }

    /**
     * Inicializa la transformación Hough. Las dimensiones de la imagen de
     * entrada son necesarias para inicializar la matriz Hough.
     *
     * @param width El ancho de la imagen de entrada
     * @param height La altura de la imagen de entrada
     */
    public HoughTransform(int width, int height) {
        initialise(width, height);
    }

    /**
     * Inicializa la matriz Hough. Llamado por el constructor para que no
     * necesite llamarlo usted mismo, sin embargo, puede usarlo para restablecer
     * la transformación si desea conectar otro imagen (aunque esa imagen debe
     * tener el mismo ancho y alto)
     */
    public void initialise(int width, int height) {
        this.width = width;
        this.height = height;

        // Calcule la altura máxima que debe tener la matriz Hough
        houghHeight = (int) (Math.sqrt(2) * Math.max(height, width)) / 2;

        // Duplique la altura de la matriz Hough para hacer frente a los valores negativos de r
        doubleHeight = 2 * houghHeight;

        // Crear la matriz Hough
        houghArray = new int[maxTheta][doubleHeight];

        // Encuentre puntos de borde y vote en matriz 
        centerX = width / 2;
        centerY = height / 2;

        // Cuenta cuantos puntos hay
        numPoints = 0;

        // almacenar en caché los valores de seno y coseno para un procesamiento más rápido
        sinCache = new double[maxTheta];
        cosCache = sinCache.clone();
        for (int t = 0; t < maxTheta; t++) {
            double realTheta = t * thetaStep;
            sinCache[t] = Math.sin(realTheta);
            cosCache[t] = Math.cos(realTheta);
        }
    }

    /**
     * Añade puntos a partir de una imagen. Se supone que la imagen es en blanco
     * y negro en escala de grises, por lo que todos los píxeles que están no
     * negros se cuentan como bordes. La imagen debe tener las mismas
     * dimensiones que la pasada al constructor.
     */
    public void addPoints(BufferedImage image) {

        // Ahora encuentre puntos de borde y actualice la matriz Hough
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // Find non-black pixels 
                if ((image.getRGB(x, y) & 0x000000ff) != 0) {
                    addPoint(x, y);
                }
            }
        }
    }

    /**
     * Agrega un solo punto a la transformación Hough. Puedes usar este método
     * directamente si sus datos no se representan como una imagen almacenada en
     * búfer.
     */
    public void addPoint(int x, int y) {

        // Ir a través de cada valor de theta
        for (int t = 0; t < maxTheta; t++) {

            // Calcule los valores r para cada paso theta
            int r = (int) (((x - centerX) * cosCache[t]) + ((y - centerY) * sinCache[t]));

            // esto hace frente a valores negativos de r
            r += houghHeight;

            if (r < 0 || r >= doubleHeight) {
                continue;
            }

            // Incrementar la matriz Hough
            houghArray[t][r]++;
        }

        numPoints++;
    }

    public Vector<HoughLine> getLines(int n) {
        return (getLines(n, 0));
    }

    /**
     * Una vez que se han agregado puntos de alguna manera, este método extrae
     * las líneas y las devuelve como un vector de objetos HoughLine, que se
     * pueden utilizar para dibujar en el
     *
     * @param: percentThreshold El umbral de porcentaje por encima del cual se
     * determinan las líneas a partir de la matriz Hough
     */
    public Vector<HoughLine> getLines(int n, int threshold) {

        // Inicializar el vector de líneas que devolveremos 
        Vector<HoughLine> lines = new Vector<HoughLine>(20);

        // Continúe solo si la matriz Hough no está vacía
        if (numPoints == 0) {
            return lines;
        }

        // Busque picos locales por encima del umbral para dibujar 
        for (int t = 0; t < maxTheta; t++) {
            loop:
            for (int r = neighbourhoodSize; r < doubleHeight - neighbourhoodSize; r++) {

                // Considere solo los puntos por encima del umbral 
                if (houghArray[t][r] > threshold) {

                    int peak = houghArray[t][r];

                    // Compruebe que este pico es de hecho el máximo local
                    for (int dx = -neighbourhoodSize; dx <= neighbourhoodSize; dx++) {
                        for (int dy = -neighbourhoodSize; dy <= neighbourhoodSize; dy++) {
                            int dt = t + dx;
                            int dr = r + dy;
                            if (dt < 0) {
                                dt = dt + maxTheta;
                            } else if (dt >= maxTheta) {
                                dt = dt - maxTheta;
                            }
                            if (houghArray[dt][dr] > peak) {
                                // encontró un punto más grande cerca, salte 
                                continue loop;
                            }
                        }
                    }

                    // calcular el verdadero valor de theta
                    double theta = t * thetaStep;

                    // agregar la línea al vector
                    lines.add(new HoughLine(theta, r, width, height, houghArray[t][r]));
                }
            }
        }
        Collections.sort(lines, Collections.reverseOrder());
        lines.setSize(n);

        return lines;
    }

    public void fitLine(HoughLine l) {
    }
}
