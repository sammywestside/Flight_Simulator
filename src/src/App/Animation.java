package App;

import java.util.ArrayList;
import java.util.Timer;

import javax.swing.JFrame;

import Utils.ApplicationTime;
import Utils.FrameUpdater;
public abstract class Animation {
    public void start() {
        ApplicationTime applicationTimeThread = new ApplicationTime();
        applicationTimeThread.start();
        FrameUpdater frameUpdater = new FrameUpdater(createFrames(applicationTimeThread));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(frameUpdater, 100, Constants.TPF);
    }
    protected abstract ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread);
}
