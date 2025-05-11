package svm.gui;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

public class FaceCaptureFrame extends JFrame {
    private static final int MAX_IMAGES = 500;
    private static final int IMAGE_SIZE = 128;

    private JTextField nameField;
    private JLabel imageLabel;
    private JButton captureButton;
    private volatile boolean capturing = false;
    private int imageCount = 0;

    public FaceCaptureFrame(Frame parent) {
        setTitle("Capture Faces");
        setLayout(new BorderLayout());
        setSize(640, 550);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        nameField = new JTextField("pseudonim", 20);
        captureButton = new JButton("Start Capture");
        imageLabel = new JLabel();

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Nume persoană:"));
        topPanel.add(nameField);
        topPanel.add(captureButton);

        add(topPanel, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);

        captureButton.addActionListener(e -> {
            if (!capturing) {
                capturing = true;
                captureButton.setEnabled(false);
                Thread captureThread = new Thread(this::startCamera);
                captureThread.setDaemon(true);
                captureThread.start();
            }
        });

        setVisible(true);
    }

    private void startCamera() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Camera nu a putut fi pornită.");
            return;
        }

        Mat frame = new Mat();
        String pseudonim = nameField.getText().trim().toLowerCase().replaceAll("[^a-z0-9_-]", "_");
        File folder = new File("svm/data/images_raw/" + pseudonim);
        if (!folder.exists() && !folder.mkdirs()) {
            JOptionPane.showMessageDialog(this, "Nu s-a putut crea folderul pentru capturi.");
            return;
        }

        while (capturing && imageCount < MAX_IMAGES) {
            camera.read(frame);
            if (!frame.empty()) {
                int width = frame.width();
                int height = frame.height();
                int x = (width - IMAGE_SIZE) / 2;
                int y = (height - IMAGE_SIZE) / 2;
                Rect roi = new Rect(x, y, IMAGE_SIZE, IMAGE_SIZE);

                Mat cropped = new Mat(frame, roi);
                Mat resized = new Mat();
                Imgproc.resize(cropped, resized, new Size(IMAGE_SIZE, IMAGE_SIZE));

                String filename = String.format("svm/data/images_raw/%s/%s_%03d.jpg", pseudonim, pseudonim, imageCount + 1);
                Imgcodecs.imwrite(filename, resized);
                imageCount++;

                // Use a neutral background instead of reddish tone
                Mat display = new Mat(frame.size(), frame.type(), new Scalar(0, 0, 0)); // black background
                cropped.copyTo(display.submat(roi));

                ImageIcon icon = new ImageIcon(matToBufferedImage(display));
                imageLabel.setIcon(icon);
                imageLabel.repaint();

                captureButton.setText("Capturing (" + imageCount + "/" + MAX_IMAGES + ")");

                try {
                    Thread.sleep(100); // ~10 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        camera.release();
        capturing = false;
        JOptionPane.showMessageDialog(this, "Capturare completă: " + imageCount + " imagini salvate.");
        dispose();
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        if (mat.channels() == 1) type = BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        return image;
    }
}
