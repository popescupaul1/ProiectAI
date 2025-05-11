package svm.util;

import svm.util.ImageSample;

import svm.util.HOGFeatureExtractor;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
//Generează fișierul out.txt conținând vectorii HOG din imaginile de antrenare (pozitive & negative).
public class GenerateHOGDataset {

    public static void main(String[] args) throws Exception {
        File posDir = new File("data/train/positive/");
        File negDir = new File("data/train/negative/");

        if (!posDir.exists() || !negDir.exists()) {
            System.out.println("Folderele data/train/positive/ sau negative/ nu exista.");
            return;
        }

        File outDir = new File("data/hog_dataset/");
        if (!outDir.exists()) outDir.mkdirs();

        File output = new File(outDir, "out.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));

        // Pozitive
        for (File f : Objects.requireNonNull(posDir.listFiles())) {
            if (!f.getName().endsWith(".jpg")) continue;

            BufferedImage img = ImageIO.read(f);
            if (img == null) {
                System.out.println("Eroare la citirea imaginii: " + f.getName());
                continue;
            }

            double[] hog = HOGFeatureExtractor.extract(img);
            for (int i = 0; i < hog.length; i++) {
                writer.write(Double.toString(hog[i]));
                if (i < hog.length - 1) writer.write(",");
            }
            writer.write(",1"); // etichetă pozitivă
            writer.newLine();
            System.out.println("Vector pozitiv salvat: " + f.getName());
        }

        //Negative
        for (File f : Objects.requireNonNull(negDir.listFiles())) {
            if (!f.getName().endsWith(".jpg")) continue;

            BufferedImage img = ImageIO.read(f);
            if (img == null) {
                System.out.println("Eroare la citirea imaginii: " + f.getName());
                continue;
            }

            double[] hog = HOGFeatureExtractor.extract(img);
            for (int i = 0; i < hog.length; i++) {
                writer.write(Double.toString(hog[i]));
                if (i < hog.length - 1) writer.write(",");
            }
            writer.write(",-1"); // etichetă negativă
            writer.newLine();
            System.out.println("Vector negativ salvat: " + f.getName());
        }

        writer.close();
        System.out.println("Dataset HOG generat în: data/hog_dataset/out.txt");
    }
}
