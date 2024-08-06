import javax.sound.sampled.*;

public class VolumeControl
      {
        private static Mixer mixer;

        private static FloatControl volumeControl;


        public VolumeControl() {
            findSpeakers();
            System.out.println("Finding speakers working");
        }


        public static void setVolume(float level) {
            if (volumeControl != null) {
                volumeControl.setValue(limit(volumeControl, level));
            }
        }

        private void findSpeakers() {
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixers) {
                if (!mixerInfo.getName().equals("Java Sound Audio Engine")) continue;
                mixer = AudioSystem.getMixer(mixerInfo);
                System.out.println(mixer + " Method inside class working");
                Line.Info[] lines = mixer.getSourceLineInfo();
                for (Line.Info info : lines) {
                    try {
                        System.out.println("In try block");
                        Line line = mixer.getLine(info);
                        line.open();
                        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                            volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                            break;
                        }
                    } catch (LineUnavailableException e) {
                        System.out.println("Error in findSpeakers method: " + e);
                        e.printStackTrace();
                    }
                }
            }
        }

        private static float limit(FloatControl control, float level) {
            return Math.min(control.getMaximum(), Math.max(control.getMinimum(), level));
        }
    }

