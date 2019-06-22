import java.util.*;
import java.io.*;
import java.util.zip.InflaterInputStream;

public class PdfStream extends PdfObject {
    private byte[] scriptByteArray;
    private String dictionary;
    private String content;
    private int dictStart;
    private int dictEnd;

    public PdfStream(byte[] scriptByteArray) {
        this.scriptByteArray = scriptByteArray;
        positionDictionary();
        StringBuilder builder = new StringBuilder("<<");
        for (int i = dictStart; i < dictEnd; i++) {
            builder.append((char) scriptByteArray[i]);
        }
        builder.append(">>");
        dictionary = builder.toString();
    }

    public void decompress(byte[] contentArray) {



        InputStream contentStream = new ByteArrayInputStream(contentArray);
        InflaterInputStream inflater = new InflaterInputStream(contentStream);
        StringBuilder contentBuilder = new StringBuilder(content);
        try {
            while (inflater.available() != 0) {
                contentBuilder.append((char) inflater.read());
            }
        } catch (IOException e) {
            // throw new IllegalArgumentException();
        }
    }

    public String getDictionary() {
        return dictionary;
    }

    private String binaryToHex(String binaryString) {
        int decimal = Integer.parseInt(binaryString, 2);
        return Integer.toString(decimal, 16);
    }

    private byte HexToDecimal(String hexString) {
        return (byte) Integer.parseInt(hexString, 16);
    }

    // post: position the start-index(inclusive) and end-index(exclusive) of the dictionary
    private void positionDictionary() {
        boolean found = false;
        int start = 0;
        for (int i = 0; i < scriptByteArray.length; i++) {
            if (i + 1 < scriptByteArray.length) {
                if (scriptByteArray[i] == (int) '<' &&
                        scriptByteArray[i + 1] == (int) '<') {
                    start = i + 2;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new IllegalArgumentException("given input is not a valid stream");
        }
        int end = 0;
        found = false;
        for (int i = start; i < scriptByteArray.length; i++) {
            if (i + 1 < scriptByteArray.length) {
                if (scriptByteArray[i] == (int) '>' &&
                        scriptByteArray[i + 1] == (int) '>') {
                    end = i;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new IllegalArgumentException("given input is not a valid stream");
        }
        dictStart = start;
        dictEnd = end;
    }
}
