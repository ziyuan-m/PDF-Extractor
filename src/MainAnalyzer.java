import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MainAnalyzer {
    private int pageNum;
    private File inputFile;

    public MainAnalyzer(File inputFile) {
        pageNum = 1;
        this.inputFile = inputFile;
    }

    public void runAnalysis(File outputFile) throws IOException {
        byte[] fileContent = Files.readAllBytes(inputFile.toPath());
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        TokenList tokenList = lexer.tokenize(fileContent);
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(tokenList);
        List<CrossReferenceTable> crossReferenceTableList = syntaxAnalyzer.extractCrossReferenceTable();
        syntaxAnalyzer.toObjectList(crossReferenceTableList);
        List<PdfObject> objectList = syntaxAnalyzer.getObjectList();
        syntaxAnalyzer.readTrailer();
        StructureTree trailer = syntaxAnalyzer.getTrailer();
        trailer.goToDictWithKey("Root");
        trailer.goToDictWithKey("Pages");
        StructureTree pageRoot = new StructureTree(trailer.getCurrentDict());
        PrintStream output = new PrintStream(outputFile);
        print(fileContent, output, objectList, pageRoot);
    }

    private void print(byte[] fileContent, PrintStream output, List<PdfObject> objectResources,
                             StructureTree pageDict) throws IOException {
        if (pageDict.valueOf("Type").equals("Page")) {
            printPageInfo(fileContent, output, objectResources, pageDict, pageNum);
            pageNum++;
        } else {
            String kids = pageDict.valueOf("Kids");
            String[] element = kids.substring(1, kids.length() - 1).split(":");
            for (int i = 0; i < element.length - 2; i += 3) {
                int innerPagesID = Integer.valueOf(element[i]);
                StructureTree innerPages = DictionaryParser.parseDict(innerPagesID, objectResources);
                print(fileContent, output, objectResources, innerPages);
            }
        }
    }

    public static void printPageInfo(byte[] fileContent, PrintStream output, List<PdfObject> objectResources,
                                     StructureTree pageDict, int pageNum) {
        for (int i = 0; i < 150; i++) {
            output.print("#");
        }
        output.println();
        output.println("#Page" + pageNum);
        String mediaboxValue = pageDict.valueOf("MediaBox");
        mediaboxValue = mediaboxValue.substring(1, mediaboxValue.length() - 1);
        String[] sizeArray = mediaboxValue.split(":");
        boolean hasDecimal = false;
        for (int i = 0; i < 4; i++) {
            if (sizeArray[i].contains(".")) {
                hasDecimal = true;
            }
        }
        if (hasDecimal) {
            String size = (Double.parseDouble(sizeArray[2]) - Double.parseDouble(sizeArray[0])) + "x" +
                    (Double.parseDouble(sizeArray[3]) - Double.parseDouble(sizeArray[1]));
            output.println("##size: " + size);
        } else {
            String size = (Integer.valueOf(sizeArray[2]) - Integer.valueOf(sizeArray[0])) + "x" +
                    (Integer.valueOf(sizeArray[3]) - Integer.valueOf(sizeArray[1]));
            output.println("##size: " + size);
        }
        pageDict.goToDictWithKey("Resources");
        StructureTree resources = new StructureTree(pageDict.getCurrentDict());
        pageDict.resetPointer();
        List<Byte> decoded = new ArrayList<>();
        if (pageDict.valueOf("Contents").startsWith("[")) {
            String contentList = pageDict.valueOf("Contents");
            String[] contents = contentList.substring(1, contentList.length() - 1).split(":");
            for (int i = 0; i < contents.length - 2; i += 3) {
                PdfObject contentStream = objectResources.get(Integer.valueOf(contents[i]));
                byte[] contentDecoded = getDecodedFromContentStream(fileContent, contentStream);
                for (byte value : contentDecoded) {
                    decoded.add(value);
                }
            }
        } else {
            PdfObject contentStream = objectResources.get(Integer.valueOf(pageDict.valueOf("Contents").split("-")[0]));
            byte[] contentDecoded = getDecodedFromContentStream(fileContent, contentStream);
            for (byte value : contentDecoded) {
                decoded.add(value);
            }
        }
//        for (byte value : contentDecoded) {
//            System.out.print((char) value);
//        }
        byte[] contentDecoded = new byte[decoded.size()];
        for (int i = 0; i < decoded.size(); i++) {
            contentDecoded[i] = decoded.get(i);
        }
        TokenList content = ContentStreamAnalyzer.tokenize(contentDecoded);
        ContentStream stream = new ContentStream(content, resources, objectResources);
        List<String> imageList = stream.getImages();
        for (String imageInfo : imageList) {
            output.println(imageInfo);
        }
        output.println("###plain text");
        String plainText = stream.getPlainText();
        for (int i = 0; i < plainText.length(); i++) {
            output.print(plainText.charAt(i));
            if (i % 100 == 0 && i != 0) {
                output.println();
            }
        }
        output.println();
        for (int i = 0; i < 120; i++) {
            output.print('-');
        }
        output.println();
        output.println("###text state table");
        List<String> textStateList = stream.getTextState();
        for (String textState : textStateList) {
            output.println(textState);
        }
    }

    private static byte[] getDecodedFromContentStream(byte[] fileContent, PdfObject contentStream) {
        TokenList streamDict = contentStream.getContent();
        int index = 0;
        String type = streamDict.getType(index);
        while (!type.equals("streamContent") && index + 1 < streamDict.size()) {
            index++;
            type = streamDict.getType(index);
        }
        int start = Integer.valueOf(streamDict.getLexeme(index).split(":")[0]);
        int end = Integer.valueOf(streamDict.getLexeme(index).split(":")[1]);
        byte[] contentOriginal = new byte[end - start + 1];
        index = 0;
        for (int i = start; i <= end; i++) {
            contentOriginal[index] = fileContent[i];
            index++;
        }
        System.out.println("start: " + start);
        System.out.println("end: " + end);
        return CompressAndDecompress.decompressZlib(contentOriginal);
    }
}
