import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public class AES {

    /**
     * Encrypt the message line (m') that will be sent to the other person using
     * cipher AES CBC
     * 
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * 
     * @see FileManager
     * @see Cipher
     */
    public static void encrypt() throws Exception {

        SecretKeySpec K = new SecretKeySpec(DiffieHellman.get("K").getBytes(), "AES");
        IvParameterSpec IV = new IvParameterSpec(DiffieHellman.get("IV").getBytes());
        String m = DiffieHellman.get("ml");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, K, IV);
        byte[] cipherText = cipher.doFinal(m.getBytes());

        DiffieHellman.record(new String[] { "cl",
                Base64.getEncoder()
                        .encodeToString(cipherText)
        });
    }

    /**
     * Decrypt the cipher received from the other person and stores it on the key
     * files as m
     * 
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * 
     * @see FileManager
     * @see Cipher
     */
    public static void decrypt() throws Exception {

        SecretKeySpec K = new SecretKeySpec(DiffieHellman.get("K").getBytes(), "AES");
        IvParameterSpec IV = new IvParameterSpec(DiffieHellman.get("IV").getBytes());
        String c = DiffieHellman.get("c");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, K, IV);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(c));

        DiffieHellman.record(new String[] { "m", new String(plainText, StandardCharsets.UTF_8) });
    }

    /**
     * Records the message line (m') to be sent back to the connection, this method
     * simply rewrite the message backwards
     * 
     * @see FileManager
     */
    public static void backwards() {
        String m = DiffieHellman.get("m");
        String ml = m + "";

        for (char letter : m.toCharArray()) {
            ml = letter + ml;
        }

        DiffieHellman.record(new String[] { "ml", ml });
    }
}
