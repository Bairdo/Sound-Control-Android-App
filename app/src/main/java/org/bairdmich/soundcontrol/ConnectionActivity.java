package org.bairdmich.soundcontrol;

import java.util.Map;

/**
 * Created by Michael on 6/12/2015.
 */
public interface ConnectionActivity {

    public void update(Map<Integer, AbstractAudioSession> audioSessions, ConnectSocketUDP server);
}
