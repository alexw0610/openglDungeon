package engine.service;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import engine.handler.AudioHandler;
import engine.object.audio.Audio;
import engine.object.audio.AudioSource;

import java.util.LinkedList;

public class AudioService {
    private static final int MAX_AUDIO_SOURCE_POOL_SIZE = 16;
    private static AudioService INSTANCE;
    private final LinkedList<AudioSource> audioSources;

    public static AudioService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioService();
        }
        return INSTANCE;
    }

    private AudioService() {
        audioSources = new LinkedList<>();
    }

    public void playAudio(Audio audio) {
        AL al = ALFactory.getAL();
        AudioSource availableSource = getAvailableAudioSource();
        if (availableSource == null && audioSources.size() < MAX_AUDIO_SOURCE_POOL_SIZE) {
            AudioSource newSource = new AudioSource();
            audioSources.add(newSource);
            availableSource = newSource;
        }
        if (availableSource != null) {
            availableSource.attachAudioToSource(audio.getAudioDataBuffer());
            al.alSourcePlay(availableSource.getSource());
        }
    }

    private AudioSource getAvailableAudioSource() {
        for (AudioSource audioSource : this.audioSources) {
            if (!audioSource.isPlaying()) {
                return audioSource;
            }
        }
        return null;
    }

}
