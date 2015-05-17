/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.player;

import net.didion.nagster.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine;

/**
 * Player that uses the JavaSound API to play WAV, AU, AIFF, and MP3
 * (using the JLayer SPI).
 */
public class SoundPlayer extends Player {
    public void play() throws Exception {
        AudioInputStream in = null;
        AudioInputStream din = null;
        SourceDataLine line = null;
        try {
            in = AudioSystem.getAudioInputStream(getInputStream());
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class, decodedFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            if (line != null) {
                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                byte[] data = new byte[4096];

                // Start
                line.open(decodedFormat);
                line.start();
                int bytesRead;
                while ((bytesRead = din.read(data, 0, data.length)) != -1) {
                    line.write(data, 0, bytesRead);
                }

                // Stop
                line.drain();
                line.stop();
            }
        } finally {
            if (line != null) try { line.close(); } catch (Exception ex) {}
            if (din != null) try { din.close(); } catch (Exception ex) {}
            if (in != null) try { in.close(); } catch (Exception ex) {}
        }
    }
}