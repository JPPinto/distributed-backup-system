package Backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Eduardo Fernandes
 */
public class Utilities {
    private static final int bufferSize = 1048576;

    /**
     * Convert byte array to hex string (Stack Overflow)
     * @param bytes Byte array to be converted to hex string
     */
    public static String convertByteArrayToHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public static String convertByteArrayToHex(byte Byte) {
        byte [] bytes = new byte[1];
        bytes[0] = Byte;
        return convertByteArrayToHex(bytes);
    }

    /**
     * Calculate file SHA256 sum
     * @param inputFile Input file
     * @return Returns file SHA-256 hash.
     * */
    public static String getHashFromFile(File inputFile) throws IOException {
        // Get buffered stream from file
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[bufferSize];

            int sizeRead;

            while ((sizeRead = bufferedInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }

            bufferedInputStream.close();

            byte[] hash = digest.digest();

            /* Convert to string */
            return convertByteArrayToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 Algorithm Not found! Aborting execution.");
            System.exit(-1);

            // Make the compiler happy, since we never get to return the empty string
            return "";
        }
    }

    public static String convertByteArrayToSring(byte[] in){
        String newString = "";

        for (int i=0; i < in.length; i++){
            newString = newString + (char) (in[i] & 0xFF);
        }

        return newString;
    }
}
