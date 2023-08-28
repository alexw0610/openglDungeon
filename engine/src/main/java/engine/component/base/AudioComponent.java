package engine.component.base;

import engine.component.Component;

public class AudioComponent implements Component {
    private static final long serialVersionUID = -3709413103820860985L;

    private String audioKey;
    boolean playOnce;
    boolean loop;

    public String getAudioKey() {
        return audioKey;
    }

    public void setAudioKey(String audioKey) {
        this.audioKey = audioKey;
    }

    public boolean isPlayOnce() {
        return playOnce;
    }

    public void setPlayOnce(boolean playOnce) {
        this.playOnce = playOnce;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
