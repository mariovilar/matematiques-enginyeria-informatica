package utils;

import java.io.*;

/**
 * Utility class for communication, providing methods to read and write data in
 * different formats.
 */
public class ComUtils {
    protected final DataInputStream dataInputStream;
    protected final DataOutputStream dataOutputStream;

    /**
     * Initializes the communication utilities with input and output streams.
     *
     * @param inputStream  Input stream to read data from.
     * @param outputStream Output stream to write data to.
     * @throws IOException If an I/O error occurs.
     */
    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    /**
     * Copy contructor for ComUtils.
     *
     * @param obj Input ComUtils obj.
     */
    public ComUtils(ComUtils obj) {
        dataInputStream = obj.dataInputStream;
        dataOutputStream = obj.dataOutputStream;
    }

    /**
     * Reads a 32-bit integer from the input stream.
     *
     * @return The integer read from the stream.
     * @throws IOException If an I/O error occurs.
     */
    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
    }

    /**
     * Writes a 32-bit integer to the output stream.
     *
     * @param number The integer to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    /**
     * Reads a string of the specified size from the input stream.
     *
     * @param size The number of characters to read.
     * @return The string read from the stream.
     * @throws IOException If an I/O error occurs.
     */
    public String read_string(int size) throws IOException {

        String result;

        byte[] bStr = read_bytes(size);
        char[] cStr = new char[size];

        for (int i = 0; i < size; i++) {
            cStr[i] = (char) bStr[i];
        }

        result = String.valueOf(cStr);

        // Trim the string to remove trailing spaces
        return result.trim();
    }

    /**
     * Writes a string to the output stream.
     *
     * @param str The string to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write_string(String str) throws IOException {

        int size = str.length();
        byte bStr[] = new byte[size];
        for (int i = 0; i < size; i++)
            bStr[i] = (byte) str.charAt(i);

        dataOutputStream.write(bStr, 0, size);
    }

    /**
     * Writes a string to the output stream, this time with a fixed byte length
     * (imposed by the specifications).
     *
     * @param str The string to write.
     * @throws IOException If an I/O error occurs.
     */
    public void write_string(String str, int allocatedBytes) throws IOException {

        int size = str.length();
        byte bStr[] = new byte[allocatedBytes];
        for (int i = 0; i < size; i++) {
            bStr[i] = (byte) str.charAt(i);
        }
        dataOutputStream.write(bStr, 0, allocatedBytes);
    }

    /**
     * Converts a 32-bit integer to a byte array using the specified endianness.
     *
     * @param number     The integer to convert.
     * @param endianness The endianness to use for conversion.
     * @return A byte array representing the integer.
     */
    protected byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if (Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte) ((number >> 24) & 0xFF);
            bytes[1] = (byte) ((number >> 16) & 0xFF);
            bytes[2] = (byte) ((number >> 8) & 0xFF);
            bytes[3] = (byte) (number & 0xFF);
        } else {
            bytes[0] = (byte) (number & 0xFF);
            bytes[1] = (byte) ((number >> 8) & 0xFF);
            bytes[2] = (byte) ((number >> 16) & 0xFF);
            bytes[3] = (byte) ((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /**
     * Converts a byte array to a 32-bit integer using the specified endianness.
     *
     * @param bytes      The byte array to convert.
     * @param endianness The endianness to use for conversion.
     * @return The converted integer.
     */
    protected int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if (Endianness.BIG_ENNDIAN == endianness) {
            number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    /**
     * Reads the specified number of bytes from the input stream.
     *
     * @param numBytes The number of bytes to read.
     * @return A byte array containing the read data.
     * @throws IOException If an I/O error occurs.
     */
    public byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes - len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /**
     * Write the specified number of bytes to the output stream.
     *
     * @param bStr The byte array to write.
     * @return void
     * @throws IOException If an I/O error occurs.
     */
    public void write_bytes(byte[] bStr) throws IOException {
        int len = bStr.length;
        dataOutputStream.write(bStr, 0, len);
    }

    public int availableBytes() throws IOException {
        return dataInputStream.available();
    }

    /**
     * Enum representing byte order endianness.
     */
    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }
}