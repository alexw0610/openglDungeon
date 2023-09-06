package engine.object.audio;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;

import static engine.service.util.AudioUtil.checkOpenALError;

public class AudioSource {
    private final int[] source = new int[1];

    public AudioSource() {
        AL al = ALFactory.getAL();
        al.alGenSources(1, this.source, 0);
        checkOpenALError(al);

        al.alSourcef(source[0], al.AL_PITCH, 1);
        al.alSourcef(source[0], al.AL_GAIN, 1);
        al.alSource3f(source[0], al.AL_POSITION, 0, 0, 0);
        al.alSource3f(source[0], al.AL_VELOCITY, 0, 0, 0);
        al.alSourcei(source[0], al.AL_LOOPING, al.AL_FALSE);
        checkOpenALError(al);
    }

    public void attachAudioToSource(int audioBuffer) {
        AL al = ALFactory.getAL();
        al.alSourcei(this.source[0], al.AL_BUFFER, audioBuffer);
        checkOpenALError(al);
    }

    public boolean isPlaying() {
        AL al = ALFactory.getAL();
        int[] sourceState = new int[1];
        al.alGetSourcei(this.source[0], al.AL_SOURCE_STATE, sourceState, 0);
        checkOpenALError(al);
        return sourceState[0] == al.AL_PLAYING;
    }

    public int getSource() {
        return this.source[0];
    }

    public void delete() {
        AL al = ALFactory.getAL();
        al.alDeleteSources(1, this.source, 0);
    }
}
