import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CompressAndDecompress {

    public static byte[] decompressZlib(byte[] inputByte) {
        int inputDataLength = inputByte.length;
        List<Byte> dataDecompressed = new ArrayList<>();
        Inflater decompressor = new Inflater();
        decompressor.setInput(inputByte, 0, inputDataLength);
        try{
            while (!decompressor.finished()) {
                byte[] buffer = new byte[inputDataLength];
                int numberOfDecompressed = decompressor.inflate(buffer);
                for (int i = 0; i < numberOfDecompressed; i++) {
                    dataDecompressed.add(buffer[i]);
                }
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        byte[] result = new byte[dataDecompressed.size()];
        for (int i = 0; i < dataDecompressed.size(); i++) {
            result[i] = dataDecompressed.get(i);
        }
        return result;
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        inflater.end();
        return output;
    }
}
