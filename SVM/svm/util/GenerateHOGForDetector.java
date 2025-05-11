package svm.util;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Objects;
import java.awt.Graphics;
public class GenerateHOGForDetector {

    public static void main(String[] args) throws Exception {
        File posDir = new File("svm/data/train/positive/");
        File negDir = new File("svm/data/train/negative/");
        File outDir = new File("svm/data/hog_dataset/");
        if (!outDir.exists()) outDir.mkdirs();
        File outFile = new File(outDir, "out.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            processImages(posDir, 1, writer);
            processImages(negDir, -1, writer);
        }

        System.out.println("Detector HOG dataset salvat în: " + outFile.getPath());
    }

    private static void processImages(File dir, int label, BufferedWriter writer) throws IOException {
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (!f.getName().endsWith(".jpg")) continue;
            BufferedImage img = ImageIO.read(f);
            if (img == null) continue;

            BufferedImage resized = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            Graphics g = resized.getGraphics();
            g.drawImage(img, 0, 0, 128, 128, null);
            g.dispose();

            double[] features = HOGFeatureExtractor.extract(resized);
            for (int i = 0; i < features.length; i++) {
                writer.write(Double.toString(features[i]));
                if (i < features.length - 1) writer.write(",");
            }
            writer.write("," + label);
            writer.newLine();
        }
    }
}
