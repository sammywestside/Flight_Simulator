package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;
import javax.swing.JFrame;
public class FrameUpdater extends TimerTask {
    private ArrayList<JFrame> frames;

    /**
     *  Create a FrameUpdater with only one Frame
     * @param frame the frame to update
     */
    public FrameUpdater(JFrame frame) {
        this.frames = new ArrayList<>();
        this.frames.add(frame);
    }

    /**
     * Create a FrameUpdater with an array of Frames
     * @param frames
     */
    public FrameUpdater (JFrame[] frames) {this.frames = new ArrayList<>(Arrays.asList(frames));}

    /**
     * Create a FrameUpdater with an ArrayList of Frames for better performance
     * @param frames
     */
    public FrameUpdater(ArrayList<JFrame> frames){
        this.frames = frames;
    }

    @Override
    public void run() {frames.forEach(f->f.repaint());}

}
