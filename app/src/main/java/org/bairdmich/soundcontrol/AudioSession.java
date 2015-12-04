package org.bairdmich.soundcontrol;

/**
 * Created by Michael on 3/02/2015.
 */
public class AudioSession implements Comparable<AudioSession> {


    private final int pid;
    private final String name;
    private int volume;

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    private boolean muted;

    //todo the image icon.

    public AudioSession(int pid, String name, int volume, boolean muted) {
        this.pid = pid;
        this.name = name;
        this.volume = volume;
        this.muted = muted;
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "PID: " + this.pid + ". Name: " + this.name + ". vol: " + volume + " muted: " + muted;
    }

    @Override
    public int compareTo(AudioSession another) {
        return this.pid - another.pid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AudioSession) {
            AudioSession a = (AudioSession) o;
            if (this.pid == a.pid) return true; // meh who cares if name and vol are the same.
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pid; // this should be unique
    }
}
