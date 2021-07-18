package util;

import java.io.*;

public class SerializableUtil {

    public static byte[] toByteArray(Serializable o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            System.err.println("Error converting serializable object to byte array");
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static Serializable fromByteArray(byte[] byteArray) {
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error converting byte array to object");
            e.printStackTrace();
        }
        return null;
    }
}
