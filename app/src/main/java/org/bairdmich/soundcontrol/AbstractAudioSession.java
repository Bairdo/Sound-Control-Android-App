package org.bairdmich.soundcontrol;

import android.support.annotation.NonNull;

/**
 * Created by Michael on 6/12/2015.
 */
public abstract class AbstractAudioSession implements Comparable<AbstractAudioSession> {

    private final int pid;
    private final String name;
    protected int volume;

    private boolean muted;

    //todo the image icon.

    public AbstractAudioSession(int pid, String name, int volume, boolean muted) {
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

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    // this return a positive int between 0-100;
    public int getVolume() {
        return volume;
    }

    public String toString() {
        return "PID: " + this.pid + ". Name: " + this.name + ". vol: " + getVolume() + " muted: " + muted;
    }

    @Override
    public int compareTo(@NonNull AbstractAudioSession another) {
        return this.pid - another.getPid();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractAudioSession) {
            AbstractAudioSession a = (AbstractAudioSession) o;
            if (this.pid == a.pid) return true; // meh who cares if name and vol are the same.
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pid; // this should be unique
    }
}
