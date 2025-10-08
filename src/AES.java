import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AES {

    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final int BLOCK_SIZE = 16; // 128 bits
    private static final Pattern WS = Pattern.compile("\\s+");

    private AES() {}

    /* ========================= PUBLIC API ========================= */

    /** Criptografa 'ml' usando chave 'K' (hex) e grava IV/C/C_PADDED. */
    public static void encrypt() throws GeneralSecurityException {
        final String mlString = requireNotBlank(DiffieHellman.get("ml"), "ml");
        final byte[] key = parseHex(requireNotBlank(DiffieHellman.get("K"), "K"));
        validateAesKey(key);

        final byte[] iv = randomIv();
        final byte[] plaintext = mlString.getBytes(StandardCharsets.UTF_8);

        final byte[] ciphertext = doCipher(Cipher.ENCRYPT_MODE, key, iv, plaintext);

        final String ivHex = Utils.encodeHexString(iv);
        final String cHex  = Utils.encodeHexString(ciphertext);

        // Padrão consistente de nomes
        DiffieHellman.record(
            new String[] {"IV", ivHex},
            new String[] {"C_PADDED", cHex},
            new String[] {"C", ivHex + cHex}
        );
    }

    /** Descriptografa usando 'IV' e 'C_PADDED' (ambos hex) para gerar 'm'. */
    public static void decrypt() throws GeneralSecurityException {
        final byte[] c   = parseHex(requireNotBlank(DiffieHellman.get("C_PADDED"), "C_PADDED"));
        final byte[] key = parseHex(requireNotBlank(DiffieHellman.get("K"), "K"));
        final byte[] iv  = parseHex(requireNotBlank(DiffieHellman.get("IV"), "IV"));
        validateAesKey(key);
        validateIv(iv);

        try {
            final byte[] plaintext = doCipher(Cipher.DECRYPT_MODE, key, iv, c);
            DiffieHellman.record(new String[] {"m", new String(plaintext, StandardCharsets.UTF_8)});
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // BadPadding tipicamente indica chave/IV ou dados incorretos
            throw new GeneralSecurityException("Falha ao descriptografar: dados ou parâmetros inválidos.", e);
        }
    }

    /** Reverte o conteúdo de 'm' e grava como 'ml'. */
    public static void backwards() {
        final String m = requireNotBlank(DiffieHellman.get("m"), "m");
        final String ml = new StringBuilder(m).reverse().toString();
        DiffieHellman.record(new String[] {"ml", ml});
    }

    /* ========================= HELPERS ========================= */

    private static byte[] doCipher(int mode, byte[] key, byte[] iv, byte[] input)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(input);
    }

    private static byte[] randomIv() {
        final byte[] iv = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static void validateAesKey(byte[] key) {
        final int len = key.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new IllegalArgumentException("Chave AES inválida: tamanho deve ser 16/24/32 bytes, mas é " + len);
        }
    }

    private static void validateIv(byte[] iv) {
        if (iv.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("IV inválido: esperado " + BLOCK_SIZE + " bytes, veio " + iv.length);
        }
    }

    private static byte[] parseHex(String s) {
        return Utils.hexStringToByteArray(WS.matcher(Objects.requireNonNull(s)).replaceAll(""));
    }

    private static String requireNotBlank(String value, String name) {
        if (value == null || WS.matcher(value).replaceAll("").isEmpty()) {
            throw new IllegalArgumentException("Valor ausente/inválido para '" + name + "'");
        }
        return value;
    }
}
