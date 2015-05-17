/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.player;

import junit.framework.TestCase;
import net.didion.nagster.Resource;
import net.didion.nagster.util.MimeType;

import java.io.File;

public class SoundPlayerTest extends TestCase {
    public SoundPlayerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.setUp();
    }

    public void testMP3() throws Exception {
        SoundPlayer player = new SoundPlayer();
        player.play(
                new Resource("mp3", MimeType.AUDIO_MP3, new File("c:/media/music/test.mp3")),
                true);
    }

    public void testWav() throws Exception {
        SoundPlayer player = new SoundPlayer();
        player.play(
                new Resource("wav", MimeType.AUDIO_WAV, new File("c:/media/music/test.wav")),
                true);
    }
}