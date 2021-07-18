package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static util.SerializableUtil.fromByteArray;
import static util.SerializableUtil.toByteArray;

public class EncryptionHandler {

    public static final String ALGORITHM = "AES";
    private final Key key;

    public EncryptionHandler(byte[] secret) {
        this.key = new SecretKeySpec(secret, ALGORITHM);
    }

    public byte[] encryptSerializableWithHeader(Serializable object) {
        byte[] request = toByteArray(object);
        System.out.println(request.length);
        return encryptByteArray(addHeader(request.length, request));
    }

    public byte[] encryptSerializable(Serializable object) {
        byte[] request = toByteArray(object);
        return encryptByteArray(request);
    }

    private byte[] encryptByteArray(byte[] request) {
        request = padArrayToMultipleOf(request, 16);
        byte[] encryptedByteArray = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedByteArray = cipher.doFinal(request);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedByteArray;
    }

    private static byte[] addHeader(int header, byte[] request) {
        byte a = (byte) header;
        byte b = (byte) (header >>> 8);
        byte[] temp = new byte[request.length + 2];
        temp[0] = a;
        temp[1] = b;
        System.arraycopy(request, 0, temp, 2, temp.length - 2);
        return temp;
    }

    private static byte[] padArrayToMultipleOf(byte[] request, int multiple) {
        int bytesToAdd = request.length % multiple;
        return Arrays.copyOf(request, request.length + (multiple - bytesToAdd));
    }

    public Serializable decryptByteArrayToObject(byte[] byteArray) {
        return fromByteArray(decryptByteArray(byteArray));
    }

    public byte[] decryptByteArray(byte[] byteArray) {
        byte[] encryptedRequest = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            encryptedRequest = cipher.doFinal(byteArray);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedRequest;
    }

}
