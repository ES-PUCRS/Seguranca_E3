import java.util.Random;
import java.math.BigInteger;

public class BigRandom {

    private static Random rand = new Random();
    private static final BigInteger maxLimit = new BigInteger(
            "999999999999999999999999999999");
    private static final BigInteger minLimit = new BigInteger("100000000000000000000000000000");
    private static final BigInteger average = maxLimit.subtract(minLimit);
    private static final int bitLength = maxLimit.bitLength();

    /**
     * Returns a random value with the desired length
     * 
     * @return BigInteger: a random BigInteger 30 digits long
     * 
     * @see BigInteger
     * @see Random
     */
    public static BigInteger get() {
        BigInteger res = new BigInteger(bitLength, rand);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(average) >= 0)
            res = res.mod(average).add(minLimit);

        return res;
    }
}
