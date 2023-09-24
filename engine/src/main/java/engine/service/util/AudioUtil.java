package engine.service.util;

import com.jogamp.openal.AL;
import engine.EntityKeyConstants;
import engine.component.base.AudioComponent;
import engine.component.base.TransformationComponent;
import engine.entity.EntityBuilder;
import org.apache.commons.lang3.RandomStringUtils;

public class AudioUtil {

    public static void createSoundEntity(String soundName, TransformationComponent transformationComponent) {
        createSoundEntity(soundName, transformationComponent.getPositionX(), transformationComponent.getPositionY());
    }

    public static void createSoundEntity(String soundName) {
        createSoundEntity(soundName, 0, 0);
    }

    private static void createSoundEntity(String soundName, double x, double y) {
        AudioComponent audio = new AudioComponent();
        audio.setPlayOnce(true);
        audio.setAudioKey(soundName);
        EntityBuilder.builder().withComponent(audio).at(x, y).buildAndInstantiate(EntityKeyConstants.AUDIO_ENTITY_PREFIX + RandomStringUtils.randomAlphanumeric(6));
    }

    public static void checkOpenALError(AL al) {
        int error = al.alGetError();
        if (error != al.AL_NO_ERROR) {
            System.err.println("openAL error code " + error);
            throw new RuntimeException();
        }
    }

}
