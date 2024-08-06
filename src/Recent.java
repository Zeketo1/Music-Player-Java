import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;

public class Recent extends JFrame {
    private DefaultListModel<String> listModel;
    private JButton closeButton;
    private JPanel mainPanel;
    private JPanel imagePanel;
    private JLabel albumImageLabel;
    private JLabel selectedSongLabel;

    public Recent(String[] musicFilePaths, ActionListener playSelectedListener) {
        super("Recently Played");

        setSize(800, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK); // Setting background color to black

        listModel = new DefaultListModel<>();
        for (String path : musicFilePaths) {
            listModel.addElement(path);
        }

        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(10);
        list.setForeground(Color.WHITE); // Set text color to white
        list.setBackground(Color.BLACK); // Set background color to black
        JScrollPane listScrollPane = new JScrollPane(list);

        closeButton = new JButton("Close");
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE); // Set button text color to white
        closeButton.setPreferredSize(new Dimension(70, 30)); // Setting preferred size for the close button

        // Create a panel for the close button to align it to the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false); // Transparent panel background
        buttonPanel.add(closeButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        imagePanel = new JPanel();
        imagePanel.setBackground(Color.BLACK);
        albumImageLabel = new JLabel();
        imagePanel.add(albumImageLabel, BorderLayout.CENTER);

        selectedSongLabel = new JLabel();
        selectedSongLabel.setForeground(Color.WHITE); // Set text color to white
        selectedSongLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align text
        selectedSongLabel.setVerticalAlignment(SwingConstants.CENTER); // Center align text

        mainPanel.add(imagePanel, BorderLayout.WEST); // Adjust layout to display image on the left
        mainPanel.add(selectedSongLabel, BorderLayout.CENTER); // Display selected song label at center
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Display button panel at the bottom

        setLayout(new BorderLayout());
        add(listScrollPane, BorderLayout.WEST); // Display list on the left
        add(mainPanel, BorderLayout.CENTER); // Display main panel at center

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedPath = list.getSelectedValue();
                selectedSongLabel.setText(getFileName(selectedPath)); // Update selected song label
                updateAlbumImage(new File(selectedPath)); // Update album image for selected song
            }
        });

        closeButton.addActionListener(e -> dispose()); // Close the window on button click

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the window only
    }

    // Method to extract file name from path
    private String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    // Method to update the album image
    private void updateAlbumImage(File audioFile) {
        try {
            AudioFile file = AudioFileIO.read(audioFile);
            Tag tag = file.getTag();
            byte[] imageData = tag.getFirstArtwork().getBinaryData();
            ImageIcon imageIcon = new ImageIcon(imageData);
            albumImageLabel.setIcon(imageIcon); // Set the album image to the JLabel
        } catch (Exception e) {
            System.out.println("Error loading album image: " + e);
        }
    }

    public static void main(String[] args) {
        // Example usage for testing
        SwingUtilities.invokeLater(() -> {
            String[] testPaths = {"Path1", "Path2", "Path3"}; // Test paths for demonstration
            ActionListener testListener = e -> System.out.println("Played: " + e.getActionCommand()); // Test action listener
            new Recent(testPaths, testListener).setVisible(true); // Create and show Recent frame
        });
    }
}
