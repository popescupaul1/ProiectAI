package svm.util;

//Structură ce stochează un vector de trăsături (HOG) și eticheta asociată (1 sau -1).
public class ImageSample {
    public double[] features;
    public int label;

    public ImageSample(double[] features, int label) {
        this.features = features;
        this.label = label;
    }
}
