public class Utils {

    /**
     * Returns a byte array read from the String given as parameter
     * 
     * @param s hexadecimal formated string
     * @return byte array formated from the hexadecimal String parameter
     */
    public static byte[] hexStringToByteArray(String s) {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Returns a String builded from the byte array given
     * 
     * @param byteArray byte array to convert to String
     * @return Hexadecimal String converted from byte array
     */
    public static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    /**
     * Returns converted byte to hexadecimal
     * 
     * @param num byte to be converted to hexadecimal
     * @return String of respective hexadecimal for the given byte
     */
    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

}
