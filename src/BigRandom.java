import java.util.Random;
import java.math.BigInteger;

public class BigRandom {

    /**
     * A simple Java Random to be used as random engine on BigInteger constructor
     */
    private static Random rand = new Random();

    /**
     * The maximum value the this BigInteger should have, being the max value of 30
     * digits
     */
    private static final BigInteger maxLimit = new BigInteger("999999999999999999999999999999");

    /**
     * The mininmun value that the BigInteger generated will have, thus it
     * represents the lowest value with 30 digits
     */
    private static final BigInteger minLimit = new BigInteger("100000000000000000000000000000");

    /**
     * The representation of the difference between the biggest and the lowest value
     */
    private static final BigInteger average = maxLimit.subtract(minLimit);

    /**
     * The bit length that the max value can reach. For those 30 digits it is 1024.
     */
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
