package engine.service.util;

import com.jogamp.openal.AL;
import engine.component.base.AudioComponent;
import engine.component.base.TransformationComponent;
import engine.entity.EntityBuilder;

public class AudioUtil {

    public static void createSoundEntity(String soundName, TransformationComponent transformationComponent) {
        AudioComponent audio = new AudioComponent();
        audio.setPlayOnce(true);
        audio.setAudioKey(soundName);
        EntityBuilder.builder()
                .withComponent(audio)
                .at(transformationComponent.getPosition().x(), transformationComponent.getPosition().y())
                .buildAndInstantiate();
    }
    public static void checkOpenALError(AL al) {
        int error = al.alGetError();
        if (error != al.AL_NO_ERROR) {
            System.err.println("openAL error code " + error);
            throw new RuntimeException();
        }
    }

}
