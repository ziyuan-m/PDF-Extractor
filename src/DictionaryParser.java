import java.util.List;

/**
 * This class contains method for parsing PDF dictionaries from tokens
 */
public class DictionaryParser {

    /**
     * Parses a PDF dictionary from the content of the object with given ID
     * @param objectID is the ID of the object that contains the tokens representing the
     *                 PDF dictionary
     * @param objectResources is the resource that contains the contents of all objects
     *                        in this PDF file
     * @return a PDF dictionary with given object ID
     */
    public static StructureTree parseDict(int objectID, List<PdfObject> objectResources) {
        TokenList inputTokens = objectResources.get(objectID).getContent();
        return parseDict(inputTokens, objectResources);
    }

    /**
     * Parses a PDF dictionary from the input tokens
     * @param inputTokes is the tokens representing the PDF dictionary
     * @param objectResources is the resource that contains the contents of all objects
     *                        in this PDF file
     * @return a PDF dictionary represented by the given tokens
     */
    public static StructureTree parseDict(TokenList inputTokes, List<PdfObject> objectResources) {
        if (!inputTokes.getLexeme(0).equals("<<") ||
                !inputTokes.getLexeme(inputTokes.size() - 1).equals(">>")) {
            throw new IllegalArgumentException("input is not a valid dictionary");
        }
        StructureTree dictionary = new StructureTree();
        parseDictHelper(dictionary, inputTokes, objectResources);
        dictionary.finish();
        return dictionary;
    }

    private static void parseDictHelper(StructureTree dictionary, TokenList inputTokens,
                                       List<PdfObject> objectResources) {
        for (int i = 1; i < inputTokens.size() - 1; i += 2) {
            String key = inputTokens.getLexeme(i);
            if (key.equals(">>")) {
                dictionary.closeCurrentDict();
                i--;
            } else {
                String value = inputTokens.getLexeme(i + 1);
                if (value.equals("<<")) {
                    dictionary.addNestedDict(key, "DirectObject");
                } else if (value.equals("[")) {
                    StringBuilder arrayContent = new StringBuilder("[");
                    String element = inputTokens.getLexeme(i + 2);
                    arrayContent.append(element);
                    int index = i + 3;
                    while (!inputTokens.getLexeme(index).equals("]") && index + 1 < inputTokens.size()) {
                        arrayContent.append(":");
                        arrayContent.append(inputTokens.getLexeme(index));
                        index++;
                    }
                    arrayContent.append("]");
                    dictionary.addNonDictValue(key, arrayContent.toString());
                    i = index - 1;
                } else if (inputTokens.getType(i + 1).equals("number") &&
                              inputTokens.getType(i + 2).equals("number")) {
                    PdfObject nestedObject = objectResources.get(Integer.valueOf(value));
                    if (nestedObject.getType() == null) {
                        dictionary.addNonDictValue(key, "null");
                    } else if (nestedObject.getType().equals("Dictionary")) {
                        if (!inputTokens.getLexeme(i).equals("Parent") && !inputTokens.getLexeme(i).equals("Prev")) {
                            dictionary.addNestedDict(key, inputTokens.getLexeme(i + 1) + "-" +
                                    inputTokens.getLexeme(i + 2));
                            parseDictHelper(dictionary, nestedObject.getContent(), objectResources);
                        }
                    } else if (nestedObject.getType().equals("List")) {
                        TokenList listTokens = nestedObject.getContent();
                        StringBuilder arrayContent = new StringBuilder("[");
                        arrayContent.append(listTokens.getLexeme(1));
                        for (int j = 2; j < listTokens.size(); j++) {
                            arrayContent.append("-");
                            arrayContent.append(listTokens.getLexeme(j));
                        }
                        if (listTokens.size() > 2) {
                            arrayContent.append("]");
                        }
                        dictionary.addNonDictValue(key, arrayContent.toString());
                    } else if (nestedObject.getType().endsWith("ContentStream")) {
                        TokenList dictContent = objectResources.get(Integer.valueOf(inputTokens.getLexeme(i + 1))).getDictPart();
                        dictionary.addNestedDict(key, inputTokens.getLexeme(i + 1) + "-" + inputTokens.getLexeme(i + 2));
                        parseDictHelper(dictionary, dictContent, objectResources);
                    } else {
                        dictionary.addNonDictValue(key, nestedObject.getContent().getLexeme(0));
                    }
                    i += 2;
                } else {
                    dictionary.addNonDictValue(key, value);
                }
            }
            if (i + 2 == inputTokens.size() - 1 && inputTokens.getLexeme(i + 2).equals(">>")) {
                dictionary.closeCurrentDict();
            }
        }
    }
}
