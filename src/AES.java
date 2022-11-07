import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.io.IOException;
import javax.crypto.Cipher;
import java.util.Arrays;

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
        String mlString, kString;
        mlString = DiffieHellman.get("ml");
        kString = DiffieHellman.get("K").replaceAll("\s", "");

        byte[] ml = mlString.getBytes();
        byte[] K = Utils.hexStringToByteArray(kString);

        BigInteger randomIV = BigRandom.get4096BitLength();
        byte[] IV = new byte[App.byteSize];
        IV = Arrays.copyOfRange(randomIV.toByteArray(), 0, App.byteSize);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(K, "AES"),
                new IvParameterSpec(IV));

        byte[] cipherText = cipher.doFinal(ml);

        String IVl = Utils.encodeHexString(IV);
        String cl = Utils.encodeHexString(cipherText);
        DiffieHellman.record(
                new String[] { "IVl", IVl },
                new String[] { "cl_PADDED", cl },
                new String[] { "cl", IVl + cl });
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
        String cString, kString, ivString;
        cString = DiffieHellman.get("c_PADDED").replaceAll("\s", "");
        kString = DiffieHellman.get("K").replaceAll("\s", "");
        ivString = DiffieHellman.get("IV").replaceAll("\s", "");

        byte[] c = Utils.hexStringToByteArray(cString);
        byte[] K = Utils.hexStringToByteArray(kString);
        byte[] IV = Utils.hexStringToByteArray(ivString);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(K, "AES"),
                new IvParameterSpec(IV));

        byte[] plainText = null;
        try {
            plainText = cipher.doFinal(c);

            DiffieHellman.record(
                    new String[] { "m", new String(plainText, StandardCharsets.UTF_8) });
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * Records the message line (m') to be sent back to the connection, this method
     * simply rewrite the message backwards
     * 
     * @throws IOException from FileManager, the file could not exist.
     * 
     * @see FileManager
     */
    public static void backwards() throws IOException {
        String m = DiffieHellman.get("m");
        String ml = "";

        for (char letter : m.toCharArray()) {
            ml = letter + ml;
        }

        DiffieHellman.record(new String[] { "ml", ml });
    }
}
