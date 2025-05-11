package svm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;

import javax.swing.JOptionPane;

import svm.gui.*;
import svm.util.FaceDetectorTrainer;
import svm.util.GenerateHOGDataset;
import svm.util.GenerateHOGForDetector;
import svm.util.GenerateHOGForRecognition;
import svm.SVMTrainer;
import svm.gui.RecognizeFromCameraFrame;

public class SVM extends Frame {

	private final int windowWidth = 1080;
	private final int windowHeight = 720;
    public Toolkit tool;
    public MenuBar mb;
    public Dimension res = new Dimension(windowWidth, windowHeight);
    public Image ico, bkg, color, calculates;

    public Design design;
    public Settings settings;
    public About about;

    public static void main(String[] args) {
        new SVM();
    }

    public SVM() {
        tool = getToolkit();
        loadImages();
        setIconImage(ico);
        setTitle("Facial Recognition App");

        addButtons();

        settings = new Settings(this);
        settings.setSize(376, 600);
        settings.setLocation((res.width - 376) / 2, (res.height - 600) / 2);

        about = new About(this);
        about.setSize(712, 410);
        about.setLocation((res.width - 712) / 2, (res.height - 410) / 2);

        setResizable(false);
        setBackground(settings.background_color);
        setSize(res);
        setLocation(0, 0);
        setVisible(true);
    }
	
	void addButtons() {
        // Create a main panel for all the buttons
        Panel mainPanel = new Panel();
        mainPanel.setLayout(null);  // Using absolute positioning for flexibility

        // Top-left buttons panel
        Panel topLeftPanel = new Panel();
        topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));  // Use FlowLayout for vertical stacking
        topLeftPanel.setBounds(20, 40, 200, 100);  // Set position (top-left)
        
        Button captureImagesButton = new Button("Capture Images");
        captureImagesButton.addActionListener(e -> handleButtonAction("Capture Images"));
        topLeftPanel.add(captureImagesButton);

        Button imageBrowserButton = new Button("Image Browser");
        imageBrowserButton.addActionListener(e -> handleButtonAction("Image Browser"));
        topLeftPanel.add(imageBrowserButton);

        mainPanel.add(topLeftPanel);

        // Top-right buttons panel (except Camera Live)
        Panel topRightPanel = new Panel();
        topRightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));  // Use FlowLayout for vertical stacking
        topRightPanel.setBounds(res.width - 220, 40, 200, 300); // Set position (top-right)

        String[] otherButtons = {
            "Generate Detector HOG", "Train Head Detector", "Generate Recognition HOG",
            "Train Multi-SVM", "About"
        };

        for (String label : otherButtons) {
            Button button = new Button(label);  // Use AWT Button
            button.addActionListener(e -> handleButtonAction(label));
            topRightPanel.add(button);
        }

        mainPanel.add(topRightPanel);

        // Bottom-right button for "Camera Live"
        Button cameraLiveButton = new Button("Camera Live");
        cameraLiveButton.addActionListener(e -> handleButtonAction("Camera Live"));
        cameraLiveButton.setBounds(res.width - 200, res.height - 100, 180, 30);  // Position at bottom-right
        mainPanel.add(cameraLiveButton);

        // Bottom-left button for "Exit"
        Button exitButton = new Button("Exit");
        exitButton.addActionListener(e -> System.exit(0));  // Close the application when clicked
        exitButton.setBounds(20, res.height - 100, 180, 30);  // Position at bottom-left
        mainPanel.add(exitButton);

        add("Center", mainPanel);  // Add the panel with buttons to the frame
    }


    public URL getResources(String s) {
        return this.getClass().getResource(s);
    }

    public void loadImages() {
        try {
            bkg = tool.getImage(getResources("res/bkg.jpg"));
            ico = tool.getImage(getResources("res/ico.png"));
            color = tool.getImage(getResources("res/color.png"));
            calculates = tool.getImage(getResources("res/calculates.gif"));
        } catch (Throwable e) {
            System.out.println("Eroare la încărcarea imaginilor!");
        }
    }

    void handleButtonAction(String cmd) {
		try {
			switch (cmd) {
				case "Capture Images":
					new FaceCaptureFrame(this);
					break;
				case "Image Browser":
					String pseudonim = JOptionPane.showInputDialog(this, "Introduceți pseudonimul:");
					if (pseudonim != null && !pseudonim.isBlank()) {
						new ImageBrowserFrame(this, pseudonim.trim().toLowerCase());
					}
					break;
				case "Generate Detector HOG":
					GenerateHOGForDetector.main(new String[0]);
					JOptionPane.showMessageDialog(this, "Vectorii HOG pentru detector generati cu succes!");
					break;
				case "Train Head Detector":
					FaceDetectorTrainer.main(new String[0]);
					JOptionPane.showMessageDialog(this, "Model antrenat și salvat cu succes!");
					break;
				case "Generate Recognition HOG":
					GenerateHOGForRecognition.main(new String[0]);
					JOptionPane.showMessageDialog(this, "Vectorii HOG pentru recunoastere generati cu succes!");
					break;
				case "Train Multi-SVM":
					TrainMultiSVM.main(new String[0]);
					JOptionPane.showMessageDialog(this, "Toate modelele au fost antrenate cu succes!");
					break;
				case "Camera Live":
					new RecognizeFromCameraFrame(this);
					break;
				case "Settings":
					settings.loadSettings();
					settings.setVisible(true);
					break;
				case "About":
					about.setVisible(true);
					break;
				case "Help":
					File help = new File("svm/SVM.pdf");
					if (help.toString().endsWith(".pdf")) {
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + help);
					} else {
						Desktop.getDesktop().open(help);
					}
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "A apărut o eroare la executarea acțiunii: " + cmd);
		}
	}

}
