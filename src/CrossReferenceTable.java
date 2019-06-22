import java.util.ArrayList;
import java.util.List;

public class CrossReferenceTable {
    private int[] range;
    private List<PdfObject> objectList;
    private boolean complete;
    private TokenList rawContent;

    public CrossReferenceTable() {
        rawContent = new TokenList();
        objectList = new ArrayList<>();
        range = new int[2];
        complete = false;
    }

    public CrossReferenceTable(TokenList content) {
        rawContent = content;
        objectList = new ArrayList<>();
        range = new int[2];
        buildTable();
    }

    public void buildTable() {
        range[0] = Integer.valueOf(rawContent.getLexeme(0));
        range[1] = Integer.valueOf(rawContent.getLexeme(1));
        for (int i = 2; i < rawContent.size(); i += 3) {
            int byteIndex = Integer.valueOf(rawContent.getLexeme(i));
            int generationNumber = Integer.valueOf(rawContent.getLexeme(i + 1));
            String state = rawContent.getLexeme(i + 2);
            PdfObject currObject = new PdfObject(byteIndex, generationNumber, state);
            objectList.add(currObject);
        }
        complete = true;
    }

    public void addContent(PdfToken inputToken) {
        rawContent.addToken(inputToken);
    }

    public List<PdfObject> getObjectList() {
        return objectList;
    }

    public int getObjectNumber() {
        return range[1];
    }
}
