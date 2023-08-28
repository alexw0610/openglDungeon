package engine.system.base;

import engine.component.base.AudioComponent;
import engine.entity.Entity;
import engine.handler.AudioHandler;
import engine.object.audio.Audio;
import engine.service.AudioService;

public class AudioSystem {
    public static void processEntity(Entity entity) {
        AudioComponent audioComponent = entity.getComponentOfType(AudioComponent.class);
        Audio audio = AudioHandler.getInstance().getAudio(audioComponent.getAudioKey());
        AudioService.getInstance().playAudio(audio);
        if (audioComponent.isPlayOnce()) {
            entity.removeComponent(AudioComponent.class);
        }

    }

    public static boolean isResponsibleFor(Entity entity) {
        return entity.hasComponentOfType(AudioComponent.class);
    }
}
