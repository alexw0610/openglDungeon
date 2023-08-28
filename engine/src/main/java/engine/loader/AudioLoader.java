package engine.loader;

import engine.object.audio.Audio;

public class AudioLoader {

    private static final String RESOURCE_AUDIO_SUBFOLDER = "./sound/";
    private static final String DEFAULT_AUDIO_FILE_EXTENSION = ".wav";

    public static Audio loadAudio(String audioName) {
        return new Audio(RESOURCE_AUDIO_SUBFOLDER + audioName + DEFAULT_AUDIO_FILE_EXTENSION);
    }
}
