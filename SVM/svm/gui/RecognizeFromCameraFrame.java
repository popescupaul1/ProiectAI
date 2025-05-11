package svm.gui;

import svm.SMOSVM; 
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import svm.util.FaceDetector;
import svm.util.HOGFeatureExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class RecognizeFromCameraFrame extends JFrame {
    private JLabel imageLabel;
    private final String detectorPath = "svm/data/models/face_detector.model";
    private final String modelsFolder = "svm/data/models";
	private final Map<String, SMOSVM> recognizers = new HashMap<>();
    private final HOGFeatureExtractor hog = new HOGFeatureExtractor();
    private final int targetWidth = 128;
    private final int targetHeight = 128;
    private final int frameDelayMs = 100;
    private volatile boolean running = true;
    private VideoCapture camera;

    public RecognizeFromCameraFrame(Frame parent) {
        setTitle("Camera Live");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(800, 600));
        imageLabel.setOpaque(true);
        add(imageLabel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                if (camera != null && camera.isOpened()) {
                    camera.release();
                }
                dispose();
            }
        });

        pack(); 
        setVisible(true);
        new Thread(this::runCameraLoop).start();
    }

    private void runCameraLoop() {
    try {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);

        // Set lower resolution for better speed
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Nu s-a putut deschide camera!");
            return;
        }

        FaceDetector faceDetector = new FaceDetector(detectorPath);
        loadAllModels();

        if (recognizers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nu au fost găsite modele pentru recunoaștere!");
            return;
        }

        Mat frame = new Mat();
        Mat smallFrame = new Mat();

        long lastProcessed = 0;
        final long minInterval = frameDelayMs; // 100ms by default

        while (running && camera.isOpened()) {
            long now = System.currentTimeMillis();
            if (now - lastProcessed < minInterval) {
                Thread.sleep(5);
                continue;
            }
            lastProcessed = now;

            camera.read(frame);
            if (frame.empty()) continue;

            Imgproc.resize(frame, smallFrame, new Size(frame.width() * 0.5, frame.height() * 0.5));
            BufferedImage buffered = matToBufferedImage(smallFrame);
            Graphics2D g = buffered.createGraphics();
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(2));

            List<Rectangle> faces = faceDetector.detectFaces(buffered);
            Map<String, Rectangle> bestByName = new HashMap<>();

            for (Rectangle rect : faces) {
                if (rect.x < 0 || rect.y < 0 || rect.width <= 0 || rect.height <= 0 ||
                    rect.x + rect.width > buffered.getWidth() ||
                    rect.y + rect.height > buffered.getHeight()) {
                    continue;
                }

                int centerX = rect.x + rect.width / 2;
                int centerY = rect.y + rect.height / 2;
                boolean isCentral = centerX > buffered.getWidth() / 4 && centerX < 3 * buffered.getWidth() / 4 &&
                                    centerY > buffered.getHeight() / 4 && centerY < 3 * buffered.getHeight() / 4;
                if (!isCentral) continue;

                try {
                    BufferedImage faceImg = buffered.getSubimage(rect.x, rect.y, rect.width, rect.height);
                    BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = resized.createGraphics();
                    g2.drawImage(faceImg, 0, 0, targetWidth, targetHeight, null);
                    g2.dispose();

                    double[] features = hog.extract(resized);
                    String recognizedName = recognizeFace(features);

                    if (!recognizedName.equals("?")) {
                        Rectangle existing = bestByName.get(recognizedName);
                        if (existing == null || rect.width * rect.height > existing.width * existing.height) {
                            bestByName.put(recognizedName, rect);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Eroare la prelucrarea feței: " + e.getMessage());
                }
            }

            for (Map.Entry<String, Rectangle> entry : bestByName.entrySet()) {
                Rectangle r = entry.getValue();
                g.drawRect(r.x, r.y, r.width, r.height);
                g.drawString(entry.getKey(), r.x, r.y - 5);
            }

            g.dispose();

            Image scaled = buffered.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.repaint();

            frame.release();
            smallFrame.release();
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }
}


	private String recognizeFace(double[] features) {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<String>> futures = new ArrayList<>();

		for (Map.Entry<String, SMOSVM> entry : recognizers.entrySet()) {
			String name = entry.getKey();
			SMOSVM model = entry.getValue();

			Callable<String> task = () -> {
				try {
					int result = model.predict(features);
					if (result == 1) return name;
				} catch (Exception e) {
					System.out.println("Predict error for " + name + ": " + e.getMessage());
				}
				return null;
			};

			futures.add(executor.submit(task));
		}

		executor.shutdown();

		for (Future<String> future : futures) {
			try {
				String result = future.get();
				if (result != null) return result;  // Return as soon as we find a match
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return " ";
	}


    private void loadAllModels() {
        File folder = new File(modelsFolder);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".model") && !name.equals("face_detector.model"));
        if (files == null) return;

        for (File file : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SMOSVM model = (SMOSVM) ois.readObject();
                String name = file.getName().replace(".model", "");
                recognizers.put(name, model);
                System.out.println("Model incarcat: " + name);
            } catch (Exception e) {
                System.out.println("Nu s-a putut incarca modelul: " + file.getName());
            }
        }
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
