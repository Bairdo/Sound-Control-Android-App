package org.bairdmich.soundcontrol;

/**
 * Created by Michael on 3/02/2015.
 */
public class AudioSession extends AbstractAudioSession {
    
    @SuppressWarnings("BooleanParameter")
    public AudioSession(int pid, String name, int volume, boolean muted) {
        super(pid, name, volume, muted);
    }
}
