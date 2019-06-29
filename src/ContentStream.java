import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a content stream
 */
public class ContentStream {
    private StringBuilder plainText;
    private StringBuilder textWithPosition;
    private int wordSpace;
    private List<double[]> positionInObject;
    private List<double[]> globalPosition;
    private List<double[]> transformationList;
    private List<String> fontList;
    private List<Integer> sizeList;
    private List<String> imageInfo;
    private TokenList content;
    private StructureTree resources;
    private List<PdfObject> objectList;
    private List<String> textStateTable;

    /**
     * Constructs a ContentStream using the given tokens
     * @param input the tokens representing the content stream
     * @param resources the resources used in this content stream
     * @param objectList all objects in this PDF file
     */
    public ContentStream(TokenList input, StructureTree resources, List<PdfObject> objectList) {
        this.objectList = objectList;
        plainText = new StringBuilder();
        textWithPosition = new StringBuilder();
        content = input;
        this.resources = resources;
        positionInObject = new ArrayList<>();
        globalPosition = new ArrayList<>();
        textStateTable = new ArrayList<>();
        String textStateTableHeader = "Font";
        for (int i = 0; i < 31; i++) {
            textStateTableHeader += " ";
        }
        textStateTableHeader += "Position";
        for (int i = 0; i < 27; i++) {
            textStateTableHeader += " ";
        }
        textStateTableHeader += "Text";
        textStateTable.add(textStateTableHeader);
        transformationList = new ArrayList<>();
        fontList = new ArrayList<>();
        sizeList = new ArrayList<>();
        imageInfo = new ArrayList<>();
        analyze();
    }

    /**
     * @return the plain text
     */
    public String getPlainText() {
        return plainText.toString();
    }

    /**
     * @return text with position
     */
    public String getTextWithPosition() {
        return textWithPosition.toString();
    }

    private void analyze() {
        for (int i = 0; i < content.size(); i++) {
            if (content.getLexeme(i).equals("BT")) {
                i = analyzeTextObject(i + 1);
            } else if (content.getLexeme(i).equals("Do")) {
                analyzeImageObject(i - 1, content.getLexeme(i - 1));
            } else if (content.getLexeme(i).equals("ET")) {
                if (globalPosition.size() > 0) {
                    positionInObject.add(globalPosition.get(globalPosition.size() - 1));
                } else {
                    positionInObject.add(new double[2]);
                }
            }
        }
    }

    private int analyzeTextObject(int startIndex) {
        int endIndex = 0;
        for (int i = startIndex; i < content.size(); i++) {
            resources.resetPointer();
            if (content.getType(i).equals("keyword")) {
                if (content.getLexeme(i).equals("Tj")) {
                    plainText.append(content.getLexeme(i - 1));
                    textWithPosition.append(content.getLexeme(i - 1));
                    if (fontList.size() > 0 && positionInObject.size() > 0) {
                        textStateTable.add(textState(fontList.get(fontList.size() - 1), positionInObject.get(positionInObject.size() - 1)[0],
                                positionInObject.get(positionInObject.size() - 1)[1], content.getLexeme(i - 1)));
                    }
                    if (content.getLexeme(i - 1).trim().length() == 0) {
                        double[] newPosition = {positionInObject.get(positionInObject.size() - 1)[0] + wordSpace,
                                                positionInObject.get(positionInObject.size() - 1)[1]};
                        positionInObject.add(newPosition);
                    }

                } else if (content.getLexeme(i).equals("TJ")) {
                    int index = i;
                    while (index >= 0 && !content.getLexeme(index).equals("[")) {
                        index--;
                    }
                    if (index < 0) {
                        throw new IllegalArgumentException("input is not valid");
                    }
                    for (int j = index + 1; j <= content.size(); j++) {
                        if (content.getLexeme(j).equals("]")) {
                            break;
                        } else if (content.getType(j).equals("string")) {
                            textWithPosition.append(content.getLexeme(j));
                            plainText.append(content.getLexeme(j));
                            if (fontList.size() > 0 && positionInObject.size() > 0) {
                                textStateTable.add(textState(fontList.get(fontList.size() - 1), positionInObject.get(positionInObject.size() - 1)[0],
                                        positionInObject.get(positionInObject.size() - 1)[1], content.getLexeme(j)));
                            }
                        } else {
                            double[] transform = {Double.parseDouble(content.getLexeme(j)), 0};
                            if (positionInObject.size() > 0) {
                                double[] lastPosition = {positionInObject.get(positionInObject.size() - 1)[0], positionInObject.get(positionInObject.size() - 1)[1]};
                                double[] newPosition = {lastPosition[0] + transform[0], lastPosition[1] + transform[1]};
                                positionInObject.add(newPosition);
                                textWithPosition.append(" <");
                                textWithPosition.append(newPosition[0]);
                                textWithPosition.append(", ");
                                textWithPosition.append(newPosition[1]);
                                textWithPosition.append(">");
                            } else {
                                positionInObject.add(transform);
                                positionInObject.add(transform);
                                textWithPosition.append(" <");
                                textWithPosition.append(transform[0]);
                                textWithPosition.append(", ");
                                textWithPosition.append(transform[1]);
                                textWithPosition.append(">");
                            }
                        }
                    }
                } else if (content.getLexeme(i).equals("Td")) {
                    int start = i - 2;
                    double xTransform = Double.parseDouble(content.getLexeme(start));
                    double yTransform = Double.parseDouble(content.getLexeme(start + 1));
                    double[] transform = {xTransform, yTransform};
                    textWithPosition.append("<");
                    textWithPosition.append(xTransform);
                    textWithPosition.append(", ");
                    textWithPosition.append(yTransform);
                    textWithPosition.append(">");
                } else if (content.getLexeme(i).equals("Tm")) {
                    int start = i - 6;
                    double[] transformationMatrix = new double[6];
                    int index = 0;
                    for (int j = start; j < start + 6; j++) {
                        if (content.getLexeme(j).contains(".")) {
                            transformationMatrix[index] = Double.parseDouble(content.getLexeme(j));
                        } else {
                            transformationMatrix[index] = Integer.valueOf(content.getLexeme(j));
                        }
                        index++;
                    }
                    transformationList.add(transformationMatrix);
                    textWithPosition.append("<reset:");
                    textWithPosition.append(transformationMatrix[4]);
                    textWithPosition.append(", ");
                    textWithPosition.append(transformationMatrix[5]);
                    textWithPosition.append(">");
                    double[] newPosition = {transformationMatrix[4], transformationMatrix[5]};
                    positionInObject.add(newPosition);
                } else if (content.getLexeme(i).equals("Tf")) {
                    int start = i - 2;
                    String fontCode = content.getLexeme(start);
                    resources.goToDictWithKey("Font");
                    resources.goToDictWithKey(fontCode);
                    String fontName = resources.valueOf("BaseFont");
                    fontList.add(fontName);
                } else if (content.getLexeme(i).equals("ET")) {
                    endIndex = i + 1;
                    if (globalPosition.size() > 0) {
                        positionInObject.add(globalPosition.get(globalPosition.size() - 1));
                    } else {
                        positionInObject.add(new double[2]);
                    }
                    break;
                } else if (content.getLexeme(i).equals("Tw")) {
                    wordSpace = Integer.valueOf(content.getLexeme(i - 1));
                }
            }
        }
        return endIndex;
    }

    private void analyzeImageObject(int index, String imageName) {
        resources.resetPointer();
        resources.goToDictWithKey("XObject");
        resources.goToDictWithKey(imageName);
        int width = Integer.valueOf(resources.valueOf("Width"));
        int height = Integer.valueOf(resources.valueOf("Height"));
        if (!content.getLexeme(index - 1).equals("cm")) {
            throw new IllegalArgumentException("input is not valid");
        }
        double[] position = new double[2];
        position[0] = Double.parseDouble(content.getLexeme(index - 3));
        position[1] = Double.parseDouble(content.getLexeme(index - 2));
        String info = "###image: w-" + width + " h-" + height + " position<" +
                       position[0] + ", " + position[1] + ">";
        imageInfo.add(info);
    }

    /**
     * @return the image information
     */
    public List<String> getImages() {
        return imageInfo;
    }

    /**
     * @return the sequence of fonts
     */
    public List<String> getFont() {
        return fontList;
    }

    /**
     * @return the sequence of text states
     */
    public List<String> getTextState() {
        return textStateTable;
    }

    private String textState(String font, double xCoord, double yCoord, String plainText) {
        String state = font;
        for (int i = 0; i < 35 - font.length(); i++) {
            state += " ";
        }
        String position = "<" + xCoord + ", " + yCoord + ">";
        state += position;
        for (int i = 0; i < 35 - position.length(); i++) {
            state += " ";
        }
        if (plainText.trim().length() > 0) {
            state += plainText;
        } else {
            state += "whitespace";
        }
        return state;
    }
}
