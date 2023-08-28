package engine.object.audio;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import java.nio.ByteBuffer;

public class Audio {

    private int[] buffer = new int[1];
    private int[] format;
    private ByteBuffer[] data;
    private int[] size;
    private int[] freq;
    private int[] loop;


    public Audio(String file) {
        AL al = ALFactory.getAL();
        this.size = new int[1];
        this.data = new ByteBuffer[1];
        this.freq = new int[1];
        this.loop = new int[1];
        this.format = new int[1];
        checkOpenALError(al);
        al.alGenBuffers(1, this.buffer, 0);
        checkOpenALError(al);
        ALut.alutLoadWAVFile(file, format, data, size, freq, loop);
        checkOpenALError(al);
        al.alBufferData(buffer[0], this.format[0], data[0], size[0], freq[0]);
        checkOpenALError(al);
        System.out.println("Loaded audio " + file + " from external path " + file);
    }

    private static void checkOpenALError(AL al) {
        int error = al.alGetError();
        if (error != al.AL_NO_ERROR) {
            System.err.println("openAL error code " + error);
        }
    }

    public int getAudioDataBuffer() {
        return this.buffer[0];
    }

    public void delete() {
        AL al = ALFactory.getAL();
        al.alDeleteBuffers(1, buffer, 0);
    }
}
