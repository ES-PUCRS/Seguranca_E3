import java.math.BigInteger;
import java.io.IOException;
import java.util.LinkedHashMap;

public class AssymetricPairKeys {

    private final LinkedHashMap<String, String> keys;
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger N;
    private final BigInteger phi;

    private static AssymetricPairKeys instance;

    private AssymetricPairKeys() throws IOException {
        keys = new LinkedHashMap<String, String>();
        p = new BigInteger(FileManager.getFileValue("p"));
        q = new BigInteger(FileManager.getFileValue("q"));
        N = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        keys.put("N", N.toString());
        keys.put("phi", phi.toString());
        keys.put("space_99", "");
    }

    private static AssymetricPairKeys instantiate() throws IOException {
        if (instance == null)
            instance = new AssymetricPairKeys();
        return instance;
    }

    public static void generate(String args[]) throws IOException {
        instantiate();
        BigInteger e, d;
        if (args.length > 1 && !args[0].isEmpty() && !args[1].isEmpty()) {
            e = new BigInteger(args[0]);
            try {
                d = e.modInverse(instance.phi);
            } catch (ArithmeticException ae) {
                throw new ArithmeticException("e: " + e.toString() + " Does not has a reverse.");
            }

        } else {
            String fileE = FileManager.getFileValue("public");
            String fileD = FileManager.getFileValue("private");
            e = (fileE == null) ? null : new BigInteger(fileE);
            d = (fileD == null) ? null : new BigInteger(fileD);

            while (e == null || d == null) {
                try {
                    e = BigRandom.get();
                    d = e.modInverse(instance.phi);
                } catch (ArithmeticException ignore) {
                }
            }

            instance.keys.put("public", e.toString());
            instance.keys.put("private", d.toString());
        }

        if (validateKeys(e, d, instance.phi, false)) {
            FileManager.publish(instance.keys);
        }
    }

    private static boolean validateKey(BigInteger n) {
        return n.equals(BigInteger.ONE);
    }

    /**
     * Returns a singleton instance of this class.
     * Since it is a simple project without code complexity and well defined code
     * patterns and fully concerned with good code pratices, there wasn't planned to
     * add this class as Singleton interface due to avoid time disposal.
     * 
     * @return FileManager: An instance from this class
     * 
     * @see https://docs.oracle.com/javaee/7/api/javax/ejb/Singleton.html
     */
    private static boolean validateKeys(BigInteger e, BigInteger d, BigInteger phi, boolean debug) {
        BigInteger GCDe = e.gcd(phi);
        BigInteger GCDd = d.gcd(phi);
        BigInteger edModPhi = e.multiply(d).mod(phi);

        if (debug) {
            System.out.println("e: " + e.toString());
            System.out.println("d: " + d.toString());
            System.out.println("gcd(e, Q(n)): " + GCDe);
            System.out.println("gcd(d, Q(n)): " + GCDd);
            System.out.println("(e * d) mod Q(n): " + edModPhi);
        }

        if (validateKey(GCDe) && validateKey(GCDd) && validateKey(edModPhi))
            return true;

        return false;
    }
}
