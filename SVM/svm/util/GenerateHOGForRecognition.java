package svm.util;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.awt.Graphics;
public class GenerateHOGForRecognition {

    public static void main(String[] args) throws Exception {
        File inputDir = new File("svm/data/images_raw/");
        File outDir = new File("svm/data/hog_dataset/");
        if (!outDir.exists()) outDir.mkdirs();

        for (File personDir : Objects.requireNonNull(inputDir.listFiles(File::isDirectory))) {
            String name = personDir.getName();
            File outFile = new File(outDir, name + ".csv");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                for (File f : Objects.requireNonNull(personDir.listFiles())) {
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
                    writer.newLine();
                }
            }

            System.out.println("Salvat HOG pentru: " + name);
        }
    }
}

