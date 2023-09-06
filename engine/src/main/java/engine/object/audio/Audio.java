package engine.object.audio;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import java.nio.ByteBuffer;

import static engine.service.util.AudioUtil.checkOpenALError;

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
        al.alGenBuffers(1, this.buffer, 0);
        checkOpenALError(al);
        ALut.alutLoadWAVFile(file, this.format, this.data, this.size, this.freq, this.loop);
        checkOpenALError(al);
        al.alBufferData(this.buffer[0], this.format[0], this.data[0], this.size[0] - (this.size[0]%8), this.freq[0]);
        checkOpenALError(al);
        System.out.println("Loaded audio " + file + " from external path " + file);
    }

    public int getAudioDataBuffer() {
        return this.buffer[0];
    }

    public void delete() {
        AL al = ALFactory.getAL();
        al.alDeleteBuffers(1, buffer, 0);
    }
}
