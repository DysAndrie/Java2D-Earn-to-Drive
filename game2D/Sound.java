package game2D;

import java.io.*;
import javax.sound.sampled.*;

// Modified by: 3140298
public class Sound extends Thread {

    String filename;    // The name of the file to play
    boolean finished;   // A flag showing that the thread has finished

    public Sound(String fname) {
        filename = fname;
        finished = false;
    }

    /**
     * run will play the actual sound but you should not call it directly.
     * You need to call the 'start' method of your sound object (inherited
     * from Thread, you do not need to declare your own). 'run' will
     * eventually be called by 'start' when it has been scheduled by
     * the process scheduler.
     */
    public void run() {
        try {
            File file = new File(filename);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat    format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
            Thread.sleep(100);
            while (clip.isRunning()) { Thread.sleep(100); }
            clip.close();
        }
        catch (Exception e) {    }
        finished = true;
    }
    
    /**
     * Plays the teleport sound effect.
     */
    public void playTeleportSound() {
        playSound("sounds/teleport.wav");
    }

    /**
     * Plays the damage sound effect.
     */
    public void playDamageSound() {
        playSound("sounds/damage.wav");
    }

    /**
     * Plays the game over sound effect.
     */
    public void playGameOverSound() {
        playSound("sounds/lose.wav");
    }
    
    /**
     * Plays the collect coin sound effect.
     */
    public void playCollectSound() {
        playSound("sounds/coin.wav");
    }

    
    /**
     * Plays the game won sound effect.
     */
    public void playGameWonSound() {
        playSound("sounds/victory.wav");
    }

    /**
     * Plays a sound effect specified by the file path.
     * 
     * @param soundFile The file path of the sound effect to be played.
     */
    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
} // End of Java Class 

//Last Modified on: 4/18/2024
//3140298

