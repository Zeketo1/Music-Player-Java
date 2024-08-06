import java.awt.*;
import javax.swing.*;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {

        SplashDemo splash = new SplashDemo();
        splash.showSplash();
    }
}

class SplashDemo extends Window {
    public SplashDemo() {
        super(null); // Use null layout
    }

    public void showSplash() {
        setSize(1200, 700);
        setLocationRelativeTo(null);
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Use FlowLayout for text labels

        try {

            // Load image using InputStream from class resource
            InputStream imageStream = getClass().getResourceAsStream("/GDMUSICIMAGE.PNG");//
            ImageIcon originalIcon = new ImageIcon(imageStream.readAllBytes());

            // Resize the image while preserving its aspect ratio
            // image is zoomed by default
            Image scaledImage = originalIcon.getImage().getScaledInstance(1200, -1, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel backgroundLabel = new JLabel(scaledIcon);
            panel.add(backgroundLabel);

            // Add text labels
            JLabel appLabel = new JLabel("GD Music Player");
            appLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            appLabel.setForeground(Color.WHITE);
            panel.add(appLabel);

            JLabel bottomLabel = new JLabel("Unleash the power of music");
            bottomLabel.setFont(new Font("Arial", Font.BOLD, 21));
            bottomLabel.setForeground(Color.WHITE);
            panel.add(bottomLabel);

            JLabel welcomeLabel = new JLabel("Welcome to GD Music Player");
            welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
            welcomeLabel.setForeground(Color.WHITE);
            panel.add(welcomeLabel);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading image: " + e.getMessage());
        }

        add(panel);

        setVisible(true);

        // Close the splash screen after 7 seconds
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create and display the main window
        new MainWindow();

        dispose(); // Dispose of the splash screen
    }
}
