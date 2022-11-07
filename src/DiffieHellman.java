import javax.xml.crypto.NoSuchMechanismException;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.math.BigInteger;
import java.io.IOException;
import java.util.Arrays;

public class DiffieHellman {

    /**
     * An instance Map that will be used to futher record all those values on the
     * file
     */
    private final LinkedHashMap<String, String> keys;

    /**
     * p is the first prime number used given the proposed document of this project
     */
    private final BigInteger p;

    /**
     * g is a module generator, thus any pow of g will be a relatively prime of
     * module p
     */
    private final BigInteger g;

    /**
     * A singleton instance to avoid many instances of this class
     */
    private static DiffieHellman instance;

    /**
     * This constructor will primarily read p and q from the keys file and have the
     * minor computation calculus under N and phi variables needed to proceed with
     * public and private key generation on further execution
     */
    private DiffieHellman() throws IOException {
        keys = new LinkedHashMap<String, String>();
        p = readHex(keys, 'p');
        g = readHex(keys, 'g');
    }

    /**
     * Return the decimal BigInteger converted from the _hex value found on keys
     * file. This is an auxiliar method to avoid code duplication. Used only on the
     * constructor
     * 
     * @param keys    The map with all file parameters
     * @param element The hex element to be read
     * @return A Decimal BigInteger of the element
     * @throws IOException from the FileManager
     */
    private static BigInteger readHex(LinkedHashMap<String, String> keys, char element) throws IOException {
        BigInteger tmp;
        String hex;

        hex = FileManager.getFileValue(element + "_hex");
        if (hex == null)
            throw new InstantiationError("There is no " + element + "_hex on keys file");
        tmp = new BigInteger(hex.replaceAll("\\s", ""), 16);
        keys.put("" + element, tmp.toString(10));

        return new BigInteger(tmp.toString(10), 10);
    }

    /**
     * Returns a singleton instance of this class.
     * Since it is a simple project without code complexity and well defined code
     * patterns and fully concerned with good code pratices, there wasn't planned to
     * add this class as Singleton interface due to avoid time disposal.
     * 
     * @return AssymetricPairKeys: An instance from this class
     * 
     * @see https://docs.oracle.com/javaee/7/api/javax/ejb/Singleton.html
     */
    private static DiffieHellman instantiate() throws IOException {
        if (instance == null)
            instance = new DiffieHellman();
        return instance;
    }

    /**
     * Records on the class Map the keys found on the file or if is not found on
     * file, generate a new pair of A and a keys ensuring that:
     * 1. a ∈ Z*p
     * 2. A = g^a mod p
     * 
     * @see https://en.wikipedia.org/wiki/Diffie–Hellman_key_exchange
     */
    public static void generatePairKeys() throws IOException {
        instantiate();
        BigInteger A, a;
        A = convertFileValue("A", 10);
        a = convertFileValue("a", 10);

        while (a == null || !a.gcd(instance.p).equals(BigInteger.ONE)) {
            a = BigRandom.get();
        }

        if (A == null)
            A = instance.g.modPow(a, instance.p);

        record(
                new String[] { "space_0", "" },
                new String[] { "a", a.toString() },
                new String[] { "A", A.toString() },
                new String[] { "A_HEX", A.toString(16) });
    }

    /**
     * Retrieve B from keys file and stores V and K on the file for further AES
     * encryption.
     * 
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void exchangeCipherKey() throws NoSuchAlgorithmException, IOException {
        String c = get("c").replaceAll("\s", "");
        BigInteger B, a, p;

        B = convertFileValue("B_HEX", 16);
        a = convertFileValue("a");
        p = convertFileValue("p");

        if (B == null || a == null || p == null)
            throw new IllegalStateException("Missing key");

        BigInteger V = B.modPow(a, p);

        byte[] K = V.toByteArray();
        byte[] KHash = hash(K);
        K = Arrays.copyOfRange(KHash, 0, App.byteSize);

        record(
                new String[] { "V", V.toString() },
                new String[] { "V_HEX", V.toString(16) },
                new String[] { "K", Utils.encodeHexString(K) },
                new String[] { "B", B.toString() },
                new String[] { "c_PADDED", c.substring(App.byteSize * 2) },
                new String[] { "IV", c.substring(0, App.byteSize * 2) });
    }

    /**
     * Returns a 128 bits of the hashed V param.
     * 
     * @param V The V = (B^a mod p) String
     * @return String: A 128 bits sliced hash of V, used as K
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * 
     * @see MessageDigest
     */
    private static byte[] hash(byte[] V) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return MessageDigest.getInstance("SHA-256")
                .digest(V);
    }

    /**
     * Returns bellow method overload
     * 
     * @param key String representation of key presented within Key file.
     * @return BigInteger: Found on file(key -> value) or null if isn't
     * @throws IOException
     * @see convertFileValue
     */
    private static BigInteger convertFileValue(String key) throws IOException {
        return convertFileValue(key, 10);
    }

    /**
     * Returns a BigInteger found from the value within keys file.
     * 
     * @param key    String representation of key presented within Key file.
     * @param system Number system that will be used (10:decimal or 16:hexadecimal)
     * @throws IOException from FileManager
     * @return BigInteger: Found on file(key -> value) or null if isn't
     * @see FileManager
     * @see BigInteger
     */
    private static BigInteger convertFileValue(String key, int system) throws IOException {
        String bi = FileManager.getFileValue(key);
        return (bi == null) ? null : new BigInteger(bi.replaceAll("\s", ""), system);
    }

    /**
     * The result of this methos is a fully record of the computed status of the
     * keys generated, recording in the keys file the module used, the phi (Euler
     * function) and both public and private key generated during the execution.
     * 
     * This method is basically a centralization of the keys & values used at the
     * file.
     * 
     * @param elements A string array of arrays with
     *                 elements[Z][0] = key && elements[Z][1] = value
     * 
     * @throws NoSuchMechanismException
     * 
     * @see FileManager
     */
    public static void record(String[]... elements) {
        if (instance == null)
            throw new NoSuchMechanismException("DiffieHellman wasn't instantiate yet.");

        for (String[] element : elements) {
            if (element != null)
                instance.keys.put(element[0], element[1]);
        }

        FileManager.publish(instance.keys);
    }

    /**
     * Retuns the key generated and stored at this class to be able to cipher and
     * decipher messages on the another class
     * 
     * @throws IOException from FileManager, file could not exist
     * 
     * @see LinkedHashMap
     */
    public static String get(String value) throws IOException {
        if (instance == null)
            return null;

        return FileManager.getFileValue(value);
    }
}
