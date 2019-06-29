import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalyzer {

    private int endOfObject;
    private int startOfTrailer;
    private TokenList tokens;
    private List<PdfObject> objectList;
    private StructureTree trailer;

    /**
     *
     * @param inputTokens
     */
    public SyntaxAnalyzer(TokenList inputTokens) {
        tokens = inputTokens;
        startOfTrailer = 1;
        endOfObject = 1;
    }

    /**
     * Convert the given list of cross-reference tables to a list of objects
     * @param crossReferenceTableList the list of cross-reference tables to be converted
     */
    public void toObjectList(List<CrossReferenceTable> crossReferenceTableList) {
        PdfObject[] objectArray = new PdfObject[crossReferenceTableList.get(crossReferenceTableList.size() - 1).getObjectNumber()];
        boolean inObject = false;
        int lastID = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (!inObject) {
                if (!tokens.getType(i).equals("comment")) {
                    if (i + 2 < tokens.size() && tokens.getType(i).equals("number")
                            && tokens.getType(i + 1).equals("number") &&
                            tokens.getLexeme(i + 2).equals("obj")) {
                        PdfObject nextObject = new PdfObject();
                        nextObject.setID(Integer.valueOf(tokens.getLexeme(i)));
                        nextObject.setGeneration(Integer.valueOf(tokens.getLexeme(i + 1)));
                        if (Integer.valueOf(tokens.getLexeme(i)) != 0) {
                            objectArray[Integer.valueOf(tokens.getLexeme(i))] = nextObject;
                            lastID = Integer.valueOf(tokens.getLexeme(i));
                        }
                        i += 2;
                        inObject = true;
                    } else {
                        endOfObject = i;
                        break;
                    }
                }
            } else {
                if (tokens.getLexeme(i).equals("endobj")) {
                    objectArray[lastID].finish();
                    inObject = false;
                } else {
                    objectArray[lastID].addTokenTo(tokens.getToken(i));
                }
            }
        }
        List<PdfObject> result = new ArrayList<>();
        for (PdfObject object : objectArray) {
            result.add(object);
        }
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i) == null) {
                result.set(i, new PdfObject(i, -1));
            }
        }
        objectList = result;
    }

    /**
     *
     * @return
     */
    public List<CrossReferenceTable> extractCrossReferenceTable() {
        List<CrossReferenceTable> tablesList = new ArrayList<>();
        boolean isInTable = false;
        for (int i = endOfObject; i < tokens.size(); i++) {
            if (!isInTable) {
                if (tokens.getLexeme(i).equals("xref")) {
                    isInTable = true;
                    i = addRangeInfo(tablesList, i);
                }
            } else {
                if (tokens.getLexeme(i).equals("trailer")) {
                    tablesList.get(tablesList.size() - 1).buildTable();
                    startOfTrailer = i + 1;
                    break;
                } else {
                    if (!tokens.getType(i + 2).equals("keyword")) {
                        tablesList.get(tablesList.size() - 1).buildTable();
                        i = addRangeInfo(tablesList, i);
                    } else {
                        for (int j = i; j < i + 3; j++) {
                            tablesList.get(tablesList.size() - 1).addContent(tokens.getToken(j));
                        }
                        i += 2;
                    }
                }
            }
        }
        return tablesList;
    }

    /**
     *
     * @return
     */
    public List<PdfObject> getObjectList() {
        return objectList;
    }

    /**
     *
     */
    public void readTrailer() {
        TokenList trailerTokens = new TokenList();
        String lexeme = tokens.getLexeme(startOfTrailer - 1);
        int index = startOfTrailer - 1;
        while (!lexeme.equals("trailer")) {
            lexeme = tokens.getLexeme(++index);
        }
        index++;
        int balance = 0;
        while (!lexeme.equals("startxref")) {
            trailerTokens.addToken(tokens.getToken(index));
            if (tokens.getLexeme(index).equals("<<")) {
                balance++;
            } else if (tokens.getLexeme(index).equals(">>")) {
                balance--;
            }
            if (balance == 0) {
                break;
            }
            index++;
            lexeme = tokens.getLexeme(index);
        }
        System.out.println(trailerTokens);
        trailer = DictionaryParser.parseDict(trailerTokens, objectList);
        trailer.finish();
    }

    /**
     *
     * @return
     */
    public StructureTree getTrailer() {
        return trailer;
    }

    private int addRangeInfo(List<CrossReferenceTable> tablesList, int index) {
        tablesList.add(new CrossReferenceTable());
        tablesList.get(tablesList.size() - 1).addContent(tokens.getToken(index + 1));
        tablesList.get(tablesList.size() - 1).addContent(tokens.getToken(index + 2));
        return index + 2;
    }
}
