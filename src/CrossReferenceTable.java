import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a cross-reference table
 */
public class CrossReferenceTable {
    private int[] range;
    private List<PdfObject> objectList;
    private boolean complete;
    private TokenList rawContent;

    /**
     * Constructs an empty CrossReferenceTable
     */
    public CrossReferenceTable() {
        rawContent = new TokenList();
        objectList = new ArrayList<>();
        range = new int[2];
        complete = false;
    }

    /**
     * Constructs a CrossReferenceTable using given content
     * @param content is the tokens that represents the cross-reference table
     */
    public CrossReferenceTable(TokenList content) {
        rawContent = content;
        objectList = new ArrayList<>();
        range = new int[2];
        buildTable();
    }

    /**
     * Adds the contents of all objects in this cross-reference table
     */
    public void buildTable() {
        // find the range of objects
        range[0] = Integer.valueOf(rawContent.getLexeme(0));
        range[1] = Integer.valueOf(rawContent.getLexeme(1));
        // build a list of objects with contents
        for (int i = 2; i < rawContent.size(); i += 3) {
            int byteIndex = Integer.valueOf(rawContent.getLexeme(i));
            int generationNumber = Integer.valueOf(rawContent.getLexeme(i + 1));
            String state = rawContent.getLexeme(i + 2);
            PdfObject currObject = new PdfObject(byteIndex, generationNumber, state);
            objectList.add(currObject);
        }
        complete = true;
    }

    /**
     * Adds a token to this CrossReferenceTable
     * @param inputToken is the token to be added
     */
    public void addContent(PdfToken inputToken) {
        rawContent.addToken(inputToken);
    }

    /**
     * @return the object list
     */
    public List<PdfObject> getObjectList() {
        return objectList;
    }


    /**
     * @return the number of objects in this cross-reference table
     */
    public int getObjectNumber() {
        return range[1];
    }
}
