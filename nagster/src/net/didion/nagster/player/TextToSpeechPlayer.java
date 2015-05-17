/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.player;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;
import net.didion.nagster.Player;

import javax.speech.EngineCreate;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import java.util.Locale;

public class TextToSpeechPlayer extends Player {
    private static final String DEFAULT_VOICE = "kevin";

    public void play() throws Exception {
        Synthesizer synth = getSynthesizer(DEFAULT_VOICE);
        synth.speakPlainText(getResource().getAsString(), null);
        synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synth.deallocate();
    }

    private static Synthesizer getSynthesizer(String voice)
            throws Exception {
        SynthesizerModeDesc desc = new SynthesizerModeDesc(
                null, "general", Locale.US, Boolean.FALSE, null);

        FreeTTSEngineCentral central = new FreeTTSEngineCentral();
        EngineList list = central.createEngineList(desc);

        Synthesizer synth = null;
        if (list.size() > 0) {
            EngineCreate creator = (EngineCreate) list.get(0);
            synth = (Synthesizer) creator.createEngine();
        }
        if (synth == null) {
            throw new Exception("Can't create synthesizer.");
        }

        synth.allocate();
        synth.resume();
        synth.getSynthesizerProperties().setVoice(getVoice(synth, voice));

        return synth;
    }

    private static Voice getVoice(Synthesizer synth, String name)
            throws Exception {
        Voice[] voices = ((SynthesizerModeDesc)
                synth.getEngineModeDesc()).getVoices();
        for (Voice v : voices) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        throw new Exception("no voice found for name " + name);
    }
}