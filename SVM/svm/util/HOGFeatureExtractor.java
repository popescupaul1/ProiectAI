package svm.util;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//Calculează vectorii de trăsături HOG pentru o imagine.
public class HOGFeatureExtractor {

    private static final int CELL_SIZE = 16;
    private static final int BLOCK_SIZE = 2;
    private static final int NUM_BINS = 9;

    public static double[] extract(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] grayscale = toGrayscale(image);
        double[][] gradX = new double[height][width];
        double[][] gradY = new double[height][width];
        double[][] magnitude = new double[height][width];
        double[][] orientation = new double[height][width];

        // Compute gradients
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                gradX[y][x] = grayscale[y][x + 1] - grayscale[y][x - 1];
                gradY[y][x] = grayscale[y - 1][x] - grayscale[y + 1][x];
                magnitude[y][x] = Math.hypot(gradX[y][x], gradY[y][x]);
                orientation[y][x] = Math.toDegrees(Math.atan2(gradY[y][x], gradX[y][x]));
                if (orientation[y][x] < 0) orientation[y][x] += 180; // range [0,180)
            }
        }

        int cellX = width / CELL_SIZE;
        int cellY = height / CELL_SIZE;

        // Cell histograms
        double[][][] histograms = new double[cellY][cellX][NUM_BINS];
        for (int cy = 0; cy < cellY; cy++) {
            for (int cx = 0; cx < cellX; cx++) {
                for (int y = 0; y < CELL_SIZE; y++) {
                    for (int x = 0; x < CELL_SIZE; x++) {
                        int px = cx * CELL_SIZE + x;
                        int py = cy * CELL_SIZE + y;
                        double angle = orientation[py][px];
                        double mag = magnitude[py][px];
                        int bin = Math.min((int)(angle / (180.0 / NUM_BINS)), NUM_BINS - 1);
                        histograms[cy][cx][bin] += mag;
                    }
                }
            }
        }

        // Normalize blocks (2x2 cells)
        List<Double> features = new ArrayList<>();
        for (int y = 0; y < cellY - 1; y++) {
            for (int x = 0; x < cellX - 1; x++) {
                double[] block = new double[NUM_BINS * BLOCK_SIZE * BLOCK_SIZE];
                int idx = 0;
                for (int dy = 0; dy < BLOCK_SIZE; dy++) {
                    for (int dx = 0; dx < BLOCK_SIZE; dx++) {
                        for (int b = 0; b < NUM_BINS; b++) {
                            block[idx++] = histograms[y + dy][x + dx][b];
                        }
                    }
                }
                double norm = 0;
                for (double v : block) norm += v * v;
                norm = Math.sqrt(norm + 1e-6); // avoid div by zero

                for (double v : block) {
                    features.add(v / norm);
                }
            }
        }

        // Convert to array
        double[] result = new double[features.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = features.get(i);
        }
        return result;
    }

    private static double[][] toGrayscale(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        double[][] result = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                result[y][x] = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
            }
        }
        return result;
    }
}
