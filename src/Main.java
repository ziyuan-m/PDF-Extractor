import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        intro();
        String inputFileName = getInputFile();
        MainAnalyzer fileAnalyzer = new MainAnalyzer(new File(inputFileName));
        fileAnalyzer.runAnalysis(new File("report.txt"));
    }

    public static void intro() {
        System.out.println("Welcome to PDF Extractor");
        System.out.println("Enter the file name and view the report in report.txt");
    }

    public static String getInputFile() {
        System.out.print("(PDF file pathname here)>> ");
        Scanner console = new Scanner(System.in);
        return console.nextLine();
    }
}
