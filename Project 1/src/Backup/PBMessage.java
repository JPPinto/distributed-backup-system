package Backup; /**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.PBMessage class
 */

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *Header
 *
 *        The header consists of a sequence of ASCII lines, sequences of ASCII codes terminated with the sequence '0x0D' '0x0A',
 * which we denote <CRLF> because these are the ASCII codes of the CR and LF chars respectively. Each line is a sequence of
 * fields, sequences of ASCII codes separated by spaces, the ASCII char ' '. The header always terminates with an empty header line.
 *
 *        In the version described herein, the header has only the following non-empty single line:
 *<MessageType> <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>
 *
 *Some of these fields may not be used by some messages, but all fields that appear in a message must appear in the
 * relative order specified above.
 *
 *        Next we describe the meaning of each field and its format.
 *
 *<MessageType>
 *This is the type of the message. Each sub protocol specifies its own message types. This field determines the format of
 * the message and what actions its receivers should perform. This is encoded as a variable length sequence of ASCII characters.
 *
 *<Version>
 *This is the version of the protocol. It is a three ASCII char sequence with the format <n>'.'<m>, where <n> and <m> are
 * the ASCII codes of digits. For example, version 1.0, the one specified in this document, should be encoded as the char sequence '1''.''0'.
 *
 *<FileId>
 *This is the file identifier for the backup service. As stated above, it is supposed to be obtained by using the SHA256
 * cryptographic getHashFromFile function. As its name indicates its length is 256 bit, i.e. 32 bytes, and should be encoded as a 64
 * ASCII character sequence. The encoding is as follows: each byte of the getHashFromFile value is encoded by the two ASCII characters
 * corresponding to the hexadecimal representation of that byte. E.g., a byte with value 0xB2 should be represented by the
 * two char sequence 'B''2' (or 'b''2', it does not matter). The entire getHashFromFile is represented in big-endian order, i.e. from
 * the MSB (byte 31) to the LSB (byte 0).
 *
 *<ChunkNo>
 *This field together with the FileId specifies a chunk in the file. The chunk numbers are integers and should be assigned
 * sequentially starting at 0. It is encoded as a sequence of ASCII characters corresponding to the decimal representation
 * of that number, with the most significant digit first. The length of this field is variable, but should not be larger
 * than 6 chars. Therefore, each file can have at most one million chunks. Given that each chunk is 64 KByte, this limits
 * the size of the files to backup to 64 GByte.
 *
 * <ReplicationDeg>
 * This field contains the desired replication degree of the chunk. This is a digit, thus allowing a replication degree of
 *  up to 9. It takes one byte, which is the ASCII code of that digit.
 */

class PBMessage {
    // Header
    // <MessageType> <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>

    // Data
    // <CRLF> <CHUNK_DATA>
    public String type;
	public String version;
	public String fileId;
	public int chunkNo;
	public int replicationDeg;
	public byte[] raw_data;
 	public Boolean validMessage;

	public static final String PUTCHUNK = "PUTCHUNK";
	public static final String STORED = "STORED";


	public PBMessage(byte[] inputData){

        int it = 0;
        String messageHeader = "";
		raw_data = inputData;

        while (true) {

            messageHeader = messageHeader + String.valueOf(inputData[it]);
            it++;

            /* Stop on first 0xD 0xA */
            if(inputData[it] == 0xDA) {
                break;
            }
        }

        /* Decode message header */
        validMessage = decodeHeaderString(messageHeader);

        /* Get data block */
        if (validMessage) {
            it++;

            if (inputData[it] == 0xDA) {
                /* Data might be present */

                it++;
                byte[] data = Arrays.copyOfRange(inputData, it, inputData.length);

            }
        }
    }

    public PBMessage(String input) {
        validMessage = decodeHeaderString(input);
    }

    private boolean decodeHeaderString(String input){

        /* Split input */
        String[] argArray = input.split(" ");
        int numberOfArgs = Array.getLength(argArray);

        /* Check number of args */
        if (numberOfArgs != 5) {
            return false;
        }

        type = argArray[0];
        version = argArray[1];
        fileId = argArray[2];
        chunkNo = Integer.getInteger(argArray[3]);
        replicationDeg = Integer.getInteger(argArray[4]);

        return validateMsgType(type) && validateVersion(version) && validateFileId(fileId) && validateChunkNo() && validateReplicationDeg();
    }

    private boolean validateMsgType(String m){

        Pattern p = Pattern.compile("\\p{Upper}+");

        return p.matcher(m).matches();
    }

    private boolean validateVersion(String v){

        /* Make use of pattern to check if input string (e.g.: 1.0, 2.4, 1.0.1)*/
        Pattern p = Pattern.compile("[0-9]+(\\.[0-9])+");

        return p.matcher(v).matches();
    }

    private boolean validateFileId(String f){
        // 64 ASCII (Hex A to F) character sequence
        Pattern p = Pattern.compile("[0-9A-F]{64}");

        return p.matcher(f).matches();
    }

    /*
     * Validate chunk number
     */
    private boolean validateChunkNo(){
        return !(chunkNo < 0 || chunkNo > 999999);
    }

    /*
     * Validate replication degree (NOT COMPLETED check upper bond)?
     */
    private boolean validateReplicationDeg(){
        return !(replicationDeg < 0 || replicationDeg > 9);
    }
}
