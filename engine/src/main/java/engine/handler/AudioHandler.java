package engine.handler;

import engine.loader.AudioLoader;
import engine.object.audio.Audio;
import engine.service.AudioService;

import java.util.HashMap;
import java.util.Map;

public class AudioHandler {
    private static AudioHandler INSTANCE;
    private final Map<String, Audio> audioMap = new HashMap<>();


    public static AudioHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioHandler();
        }
        return INSTANCE;
    }

    private AudioHandler() {
    }

    public Audio getAudio(String audioName) {
        if (!audioName.isEmpty()) {
            if (audioMap.containsKey(audioName)) {
                return audioMap.get(audioName);
            } else {
                Audio audio = AudioLoader.loadAudio(audioName);
                audioMap.put(audioName, audio);
                return audio;
            }
        }
        return null;
    }
}
