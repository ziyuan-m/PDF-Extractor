import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HexClear {
    public static void pureHex(File hexFile) throws IOException{
        Scanner input = new Scanner(hexFile);
        List<String> output = new ArrayList<>();
        while (input.hasNextLine()) {
            String currLine = input.nextLine();
            System.out.println(currLine);
            int start = currLine.indexOf(":") + 1;
            int end = currLine.indexOf(";");
            output.add(currLine.substring(start, end));
        }
        FileOutputStream fileOutputStream = new FileOutputStream(hexFile);
        fileOutputStream.close();
        PrintStream outputStream = new PrintStream(hexFile);
        for (String line : output) {
            outputStream.println(line);
        }
    }
}
