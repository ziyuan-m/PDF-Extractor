import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Test {
    public static void main(String[] args) throws FileNotFoundException, IOException, DataFormatException {
//        long startTime = System.currentTimeMillis();
//        byte[] fileContent = Files.readAllBytes(new File("test7.pdf").toPath());
//        LexicalAnalyzer lexer = new LexicalAnalyzer();
//        TokenList tokenList = lexer.tokenize(fileContent);
//        PrintStream output = new PrintStream(new File("output.txt"));
//        output.println(tokenList);
//        long endTime = System.currentTimeMillis();
//        System.out.println("time elapsed: " + (endTime - startTime) + "ms");
//        SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokenList);
//        List<CrossReferenceTable> crossReferenceTableList = analyzer.extractCrossReferenceTable();
//        analyzer.toObjectList(crossReferenceTableList);
//        List<PdfObject> objectList = analyzer.getObjectList();
//        System.out.println(objectList.size());
//
//
//        SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokenList);
//        analyzer.toObjectList();
//        List<PdfObject> objectList = analyzer.getObjectList();
//        Collections.sort(objectList);
//        analyzer.extractCrossReferenceTable();
//        TokenList dictTokens = objectList.get(5).getContent();
//        // analyzer.readTrailer();
//        // long endTime = System.currentTimeMillis();
//        // System.out.println("time elapsed: " + (endTime - startTime) + "ms");
//
//        System.out.println(dictTokens);
//        System.out.println("##-----------------------------------------------------##");
//        StructureTree dict = DictionaryParser.parseDict(dictTokens, objectList);
//        System.out.println(dict);

        byte[] fileContent = Files.readAllBytes(new File("test7.pdf").toPath());
        int start = 559;
        int end = 2877;
        byte[] inputByte = new byte[end - start + 1];
        int index = 0;
        for (int i = start; i <= end; i++) {
            inputByte[index] = fileContent[i];
            index++;
        }
        System.out.println(inputByte[0] + ", " + inputByte[1]);
        byte[] outputByte = CompressAndDecompress.decompress(inputByte);
        System.out.println("done");
        OutputStream output = new FileOutputStream(new File("output1.txt"));
        for (int i = 0; i < outputByte.length; i++) {
            output.write(outputByte[i]);
        }

//        System.out.println(LexicalResource.isNumber("0.6"));
//        String[] operator = {"\"", "'", "cm", "BT", "Do", "ET", "T*", "TD", "TJ", "TL", "Tc", "Td",
//                "Tf", "Th", "Tj", "Tm", "Tr", "Ts", "Tw", "Tz"};
//        Arrays.sort(operator);
//        for (String s : operator) {
//            System.out.println(s);
//        }
//        for (int i = 0; i < 10; i++) {
//            decompressByteArrayTest();
//        }


    }

    public static void decompressByteArrayTest() throws IOException, DataFormatException{
        byte[] input = new byte[1024];
        byte[] output, result;
        new Random().nextBytes(input);
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        output = outputStream.toByteArray();
        deflater.end();
        System.out.println(output[0] + ", " + output[1]);
        result = CompressAndDecompress.decompress(output);
    }

    public static String toHex(int decimal){
        int rem;
        String hex="";
        char hexchars[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(decimal>0)
        {
            rem=decimal%16;
            hex=hexchars[rem]+hex;
            decimal=decimal/16;
        }
        return hex;
    }

    public static String HexToBinary() throws FileNotFoundException{
        String binary = "";
        Scanner input = new Scanner(new File("hex.txt"));
        int position = 0;
        while (input.hasNext()) {
            String hex = input.next();
            try {
                int decimal = (int) Long.parseLong(hex, 16);
                binary += Integer.toBinaryString(decimal);
                position++;
            } catch (NumberFormatException e) {
                System.err.println("at position " + position);
                position++;
            }
        }
        return binary;
    }

    public static byte[] binaryToByteArray(String binaryString) {
        byte[] result = new byte[1000];
        int index = 0;
        while (binaryString.length() >= 8) {
            String currentBinary = binaryString.substring(0, 8);
            result[index] = (byte) Integer.parseInt(currentBinary, 2);
            binaryString = binaryString.substring(8);
            index++;
        }
        return result;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }
}
