import java.io.IOException;

public class App {

    /**
     * Size of bits desired to be sliced from the hash that will become the AES Key
     */
    public static final int bitSize = 128;

    /**
     * Size of bytes desired to be sliced from the hash that will become the AES Key
     */
    public static final int byteSize = bitSize / 8;

    /**
     * The root keys.txt file should contain the following parameters:
     * 
     * Variables needed to generate pair keys:
     * - p_hex: (with or without spaces) a hexadecimal value given on the job
     * document;
     * - g_hex: (with or without spaces) relative prime number generator that was
     * given on the job document;
     * 
     * Variables needed to decript message:
     * B_HEX: hexadecimal value sent from the peer
     * c: encrypted message
     * 
     */
    public static void main(String args[]) throws Exception {
        DiffieHellman.generatePairKeys();

        if (exist("B_HEX") && exist("c"))
            DiffieHellman.exchangeCipherKey();

        if (exist("c_PADDED") && exist("K") && exist("IV"))
            AES.decrypt();

        if (exist("m"))
            AES.backwards();

        AES.encrypt();
    }

    private static boolean exist(String key) throws IOException {
        return DiffieHellman.get(key) != null;
    }
}
