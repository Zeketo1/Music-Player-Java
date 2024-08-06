    import java.awt.*;
    import java.awt.event.*;
    import javax.sound.midi.Receiver;
    import javax.sound.midi.Sequence;
    import javax.sound.midi.Sequencer;
    import javax.sound.midi.Synthesizer;
    import javax.swing.*;
    import javax.sound.sampled.*; //###
    import java.io.*;
    import javazoom.jl.decoder.JavaLayerException;
    import javazoom.jl.player.Player;
    import org.jaudiotagger.audio.AudioFile;
    import org.jaudiotagger.audio.AudioFileIO;
    import org.jaudiotagger.tag.Tag;
    import org.w3c.dom.ls.LSOutput;

    import javax.swing.JProgressBar;
    import javax.swing.event.ChangeEvent;
    import javax.swing.event.ChangeListener; //###

    public class MainWindow extends JFrame {
        private String[] musicFilePaths = {

                // empty for now!
        };
        private JLabel imageLabel;
        private JLabel statusLabel;


        private Sequencer seqr;//###
        private Sequence seq;//###
        private Receiver receiver;//###
        private boolean usingHardwareSoundbank;//###
        private Synthesizer synth;//###


        private JProgressBar progressBar;


        public VolumeControl volumeControl;
        private Timer timer;//###
        int progress = 0;

        private JPanel controlPanel;

        private JSlider volumeSlider; //###
        private JButton playButton;
        private JButton recentButton;

        private float volumeLevel = 0.0f;//###

        private JButton muteButton;


        private boolean isMuted = false; // Tracks whether the audio is muted


        private FileInputStream fis;
        private long pausedPosition = 0;


        private JButton stopButton;
        private JButton pauseButton;
        private JButton prevButton;
        private JButton nextButton;
        private JButton openFileButton;
        private JPanel imageContainer;
        private String selectedFilePath;
        private Player player;
        private Thread playerThread;



        private int currentFileIndex = 0;

        public MainWindow() {

            super("GDMusic Player");

            setSize(1300, 600);


            setLocationRelativeTo(null);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            progressBar = new JProgressBar(0, 100);
            // Add the progress bar to the control panel



            //###



            timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (player != null && fis != null) {
                        try {
                            // Example values, you'll need to replace these with actual calculations
                            long totalLength = fis.getChannel().size();
                            long currentPosition = fis.getChannel().position();
                            progress = 0;
                            progress = (int)(((double)currentPosition / (double)totalLength) * 100);

                            progressBar.setValue(progress);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            muteButton = new JButton("Mute");
            muteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    toggleMute();
                }
            });

            playButton = new JButton("Play");
            stopButton = new JButton("Stop");
            pauseButton = new JButton("Pause");
            prevButton = new JButton("Prev");
            nextButton = new JButton("Next");
            openFileButton = new JButton("Open File");
            statusLabel = new JLabel("No file selected");
            recentButton = new JButton("Recently Played ");
            playButton.setBackground(new Color(77, 175, 124)); // Green color
            stopButton.setBackground(Color.RED);
            pauseButton.setBackground(new Color(250, 180, 45)); // Yellow color
            prevButton.setBackground(new Color(63, 81, 181)); // Blue color
            nextButton.setBackground(new Color(233, 30, 99)); // Pink color
            openFileButton.setBackground(new Color(96, 125, 139)); // Steel blue color
            recentButton.setBackground(new Color(255, 152, 0)); // Orange


            statusLabel.setForeground(Color.BLACK); // Set text color of status label to white
            playButton.setForeground(Color.WHITE); // Set text color of play button to white
            stopButton.setForeground(Color.WHITE); // Set text color of stop button to white
            pauseButton.setForeground(Color.WHITE); // Set text color of pause button to white
            prevButton.setForeground(Color.WHITE); // Set text color of prev button to white
            nextButton.setForeground(Color.WHITE); // Set text color of next button to white
            openFileButton.setForeground(Color.WHITE); // Set text color of open file button to white
            recentButton.setForeground(Color.WHITE); // Set text color of recent button to white
            imageContainer = new JPanel();
            imageContainer.setBackground(Color.BLACK);
            imageContainer.setLayout(new BorderLayout());
    //###
            // Volume slider initialization
            volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            volumeSlider.setMajorTickSpacing(10);
            volumeSlider.setMinorTickSpacing(5);
            volumeSlider.setPaintTicks(true);
            volumeSlider.setPaintLabels(true);
            volumeSlider.setPreferredSize(new Dimension(200, 50));
    //###

            JPanel controlPanel = new JPanel();
            controlPanel.setLayout(new FlowLayout());
            controlPanel.add(playButton);
            controlPanel.add(stopButton);
            controlPanel.add(pauseButton);
            controlPanel.add(prevButton);
            controlPanel.add(nextButton);
            controlPanel.add(recentButton);
            controlPanel.add(openFileButton);
            controlPanel.add(statusLabel);
            controlPanel.add(progressBar);
            controlPanel.add(muteButton);
            controlPanel.add(volumeSlider);//###

            progressBar.setValue(0);
            progressBar.setStringPainted(true);


            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(imageContainer, BorderLayout.CENTER);
            getContentPane().add(controlPanel, BorderLayout.SOUTH);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);

            openFileButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openFile();
                }
            });

            recentButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Recent recentWindow = new Recent(musicFilePaths, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selectedFilePath = (String) e.getActionCommand();
                            playSelectedFile(new File(selectedFilePath));
                        }
                    });
                    recentWindow.setVisible(true);
                }
            });


            playButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    play();
                }
            });
            // Add volume slider change listener


            //###
            // Change volumeControl instantiation from volumeontrol to volumeControl
            volumeControl = new VolumeControl();

// Change volumeSlider change listener to create an instance of VolumeControl and call setVolume on that instance
            // Change volumeSlider change listener to use the volumeControl instance
            volumeSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int value = volumeSlider.getValue();
                    float level = value / 100.0f; // Convert to float between 0.0 and 1.0
                    volumeControl.setVolume(level); // Call setVolume on volumeControl instance
                }
            });


            stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop();
                }
            });

            pauseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pause();
                }
            });

            volumeSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int value = volumeSlider.getValue();
                    float level = value / 100.0f; // Convert to float between 0.0 and 1.0
                    VolumeControl.setVolume(level);
                }
            });

            prevButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    prev();
                }
            });

            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    next();
                }
            });
        }




        private void openFile() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFilePath = selectedFile.getAbsolutePath(); // Store the selected file path
                statusLabel.setText("Playing: " + selectedFile.getName());
                pausedPosition = 0;
                updateAlbumImage(selectedFile);
                playSelectedFile(selectedFile);

                // Add selected file path to the musicFilePaths array
                addFilePath(selectedFilePath);
            }
        }


        private void playSelectedFile(File file) {
            try {
                if (fis != null) {
                    fis.close(); // Close the previous FileInputStream if open
                }
                fis = new FileInputStream(file); // Initialize the instance variable fis
                BufferedInputStream bis = new BufferedInputStream(fis);

                // Create a new Player instance for the current file
                player = new Player(bis);
                progress = 0;


                // Start the Timer to update playback position
                timer.start();

                playerThread = new Thread(() -> {
                    try {
                        // Skip the initial part to resume playback from the paused position
                        fis.skip(pausedPosition);
                        player.play();
                    } catch (JavaLayerException e) {
                        // Error handling...
                    } catch (IOException e) {
                        // Error handling...
                    } finally {
                        if (player != null) {
                            try {
                                pausedPosition = fis.available(); // Update pausedPosition with the current position
                                player.close(); // Close the player after playback
                            } catch (IOException e) {
                                // Error handling...
                            }
                        }
                        // Stop the Timer after playback ends
                        timer.stop();
                    }
                });
                playerThread.start();

                // Add the selected file path to the musicFilePaths array
                addFilePath(file.getAbsolutePath());

            } catch (FileNotFoundException e) {
                // File not found handling...
            } catch (IOException e) {
                // Error initializing player handling...
            } catch (JavaLayerException e) {
                // Exception handling...
            }
            addFilePath(file.getAbsolutePath());
        }









        private void toggleMute() {
            isMuted = !isMuted; // Toggle mute state
            if (isMuted) {
                muteButton.setText("Unmute");
                muteAudio(true); // Mute the audio
            } else {
                muteButton.setText("Mute");
                muteAudio(false); // Unmute the audio
            }
        }






        private void stop() {
            if (player != null && playerThread != null) {
                player.close();
                playerThread.interrupt();
                statusLabel.setText("Stopped");
                pausedPosition = 0; // Reset the paused position
                progress = 0;
            }
        }

        private void pause() {
            if (player != null && playerThread != null && playerThread.isAlive()) {
                // Assuming you can calculate pausedPosition accurately
                try {
                    pausedPosition = fis.getChannel().position(); // Save current position
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.close();
                playerThread.interrupt();
                timer.stop(); // Stop the Timer immediately on pause
                statusLabel.setText("Paused");
            }
        }




        private void muteAudio(boolean mute) {
            try {
                Mixer.Info[] infos = AudioSystem.getMixerInfo();
                for (Mixer.Info info : infos) {
                    Mixer mixer = AudioSystem.getMixer(info);
                    Line.Info[] lineInfos = mixer.getTargetLineInfo();
                    for (Line.Info lineInfo : lineInfos) {
                        Line line = mixer.getLine(lineInfo);
                        line.open();
                        if (line.isControlSupported(BooleanControl.Type.MUTE)) {
                            BooleanControl bc = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                            bc.setValue(mute);
                        }
                        line.close();
                    }
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }



        private void addFilePath(String filePath) {
            // Check if the filePath is already in the array
            for (String path : musicFilePaths) {
                if (path.equals(filePath)) {
                    return; // File path already exists, no need to add again
                }
            }
            // Expand the array by one to accommodate the new file path
            String[] newFilePaths = new String[musicFilePaths.length + 1];
            System.arraycopy(musicFilePaths, 0, newFilePaths, 0, musicFilePaths.length);
            newFilePaths[newFilePaths.length - 1] = filePath; // Add the new file path to the end
            musicFilePaths = newFilePaths; // Update the musicFilePaths array with the new paths
        }


        private void play() {
            if (selectedFilePath == null || selectedFilePath.isEmpty()) {
                System.out.println("No file selected");
                return;
            }

            File file = new File(selectedFilePath);
            if (!file.exists()) {
                System.out.println("File not found: " + selectedFilePath);
                statusLabel.setText("File not found: " + selectedFilePath);
                return;
            }

            if (player != null && playerThread != null && playerThread.isAlive()) {
                return; // Player is already playing, do nothing
            }

            // Start playing from the saved position
            playSelectedFile(file);
            // Resume playback from the exact saved position
            statusLabel.setText("playing"+ file.getName());

        }
        private void updateAlbumImage(File audioFile) {
            try {
                AudioFile file = AudioFileIO.read(audioFile);
                Tag tag = file.getTag();
                byte[] imageData = tag.getFirstArtwork().getBinaryData();
                ImageIcon imageIcon = new ImageIcon(imageData);
                JLabel backgroundImageLabel = new JLabel(imageIcon);
                imageContainer.removeAll(); // Remove any existing components
                imageContainer.add(backgroundImageLabel, BorderLayout.CENTER);
                imageContainer.revalidate(); // Refresh the layout
            } catch (Exception e) {
                System.out.println("Error loading album image: " + e);
            }
        }
        private void prev() {
            currentFileIndex = (currentFileIndex - 1 + musicFilePaths.length) % musicFilePaths.length;
            selectedFilePath = musicFilePaths[currentFileIndex];
            stop(); // Stop playback before playing the new file
            updateAlbumImage(new File(selectedFilePath)); // Update the album image
            playSelectedFile(new File(selectedFilePath));
            progress = 0;
        }

        private void next() {
            currentFileIndex = (currentFileIndex + 1) % musicFilePaths.length;
            selectedFilePath = musicFilePaths[currentFileIndex];
            stop(); // Stop playback before playing the new file
            updateAlbumImage(new File(selectedFilePath)); // Update the album image
            playSelectedFile(new File(selectedFilePath));
            progress = 0;
        }
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> new MainWindow());
        }

    }