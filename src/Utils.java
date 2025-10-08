public class Utils {

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hexString Hexadecimal string (must have even length).
     * @return Byte array corresponding to the hexadecimal string.
     * @throws IllegalArgumentException if the input is null, has odd length, or contains invalid characters.
     */
    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }

        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int hi = Character.digit(hexString.charAt(i), 16);
            int lo = Character.digit(hexString.charAt(i + 1), 16);
            if (hi == -1 || lo == -1) {
                throw new IllegalArgumentException("Invalid hex digit: " + hexString.substring(i, i + 2));
            }
            data[i / 2] = (byte) ((hi << 4) + lo);
        }

        return data;
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param byteArray The byte array to convert.
     * @return A hexadecimal string representation of the byte array.
     * @throws IllegalArgumentException if byteArray is null.
     */
    public static String encodeHexString(byte[] byteArray) {
        if (byteArray == null) {
            throw new IllegalArgumentException("Input byte array cannot be null");
        }

        StringBuilder hexString = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            hexString.append(byteToHex(b));
        }
        return hexString.toString();
    }

    /**
     * Converts a byte to a 2-character hexadecimal string.
     *
     * @param b The byte to convert.
     * @return A 2-character string representing the byte in hexadecimal (lowercase).
     */
    public static String byteToHex(byte b) {
        char[] hexDigits = {
            Character.forDigit((b >> 4) & 0xF, 16),
            Character.forDigit(b & 0xF, 16)
        };
        return new String(hexDigits);
    }
}
