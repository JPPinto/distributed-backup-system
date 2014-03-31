package Backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Misc Utilities
 */
public class Utilities {
	/* Path constants */
	public static final String backupDirectory = "./backup";
	public static final String temporaryDirectory = "./temporary";
	private static final int bufferSize = 1048576;
	/* Buffer size for hashing operations 1MiB */
	static final int chunkDataSize = 64000;

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

        for (byte anIn : in) {
            newString += (char) (anIn & 0xFF);
        }

        return newString;
    }

    public static byte[] convertStringToByteArray(String in){
        return in.getBytes(Charset.forName("UTF-8"));
    }

    public static byte[] joinTwoArrays(byte[] arrayA, byte[] arrayB){
        byte[] merged = new byte[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, merged, 0, arrayA.length);
        System.arraycopy(arrayB, 0, merged, arrayA.length, arrayB.length);
        return merged;
    }

    /*
     * Validate SHA-256 SUM
     */
    public static boolean validateFileId(String f) {
        // 64 ASCII (Hex A to F) character sequence
        Pattern p = Pattern.compile("[0-9a-fA-F]{64}");
        return p.matcher(f).matches();
    }

    /*
     * Validate chunk number
     */
    public static boolean validateChunkNo(int chunkNo) {
        return !(chunkNo < 0 || chunkNo > 999999);
    }

    /*
     * Validate replication degree (NOT COMPLETED check upper bond)?
     */
    public static boolean validateReplicationDeg(int replicationDeg) {
        return !(replicationDeg < 0 || replicationDeg > 9);
    }

	/**
     * Reads a file and creates chunks
     * @param inputFile Input file
     **/
    public static void readChunks(File inputFile,String directory) throws IOException {
        // Get fileId
        String fileID = getHashFromFile(inputFile);

        // Get buffered stream from file
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        long fileSize = inputFile.length();

        byte[] buffer = new byte[chunkDataSize];

        int sizeRead;
        int currentChunkNumber = 0;

        while ((sizeRead = bufferedInputStream.read(buffer)) != -1) {

            byte[] realData = Arrays.copyOfRange(buffer, 0, sizeRead);

            Chunk currentChunk = new Chunk(fileID, currentChunkNumber, realData);
            currentChunk.write(directory);

            currentChunkNumber++;

        }
        bufferedInputStream.close();

        if (fileSize % chunkDataSize == 0) {
            Chunk finalChunk = new Chunk(fileID, currentChunkNumber);
            finalChunk.write(directory);
        }
    }

	/**
     * Lists local files
     * @param path Folder path
     */
    public static File[] listFiles(String path) {
        File folder = new File(path);
        return folder.listFiles();
    }
}
