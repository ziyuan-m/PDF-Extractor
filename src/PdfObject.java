/**
 * This class represents an object in PDF. It stores the content of the object
 * as tokens. It also stores metadata about the object such as ID, generation number,
 * and position in the binary file.
 */
public class PdfObject implements Comparable<PdfObject> {
    private String type;
    private TokenList content;
    private int ID;
    private int generation;
    private int byteIndex;
    private boolean isInUse;
    private boolean complete;

    public PdfObject(int ID, int generation) {

    }

    /**
     * Constructs a new PdfObject with given position, generation number and state
     * @param byteIndex the position of the object
     * @param generation generation number
     * @param stateIndicater the state of the object (on/off)
     */
    public PdfObject(int byteIndex, int generation, String stateIndicater) {
        this.byteIndex = byteIndex;
        this.generation = generation;
        isInUse = stateIndicater.equals("n");
        type = "notDefined";
    }

    /**
     * Constructs an empty PdfObject
     */
    public PdfObject() {
        byteIndex = -1;
        content = new TokenList();
        complete = false;
        type = "notDefined";
    }

    /**
     * Sets the ID of this PdfObject as given
     * @param ID >= 0
     */
    public void setID(int ID) {
        if (ID < 0) {
            throw new IllegalArgumentException("given ID is negative");
        }
        this.ID = ID;
    }

    /**
     * @return the ID of this PdfObject
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets the generation number as given
     * @param generation generation number
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     *
     * @return the generation number of this PdfObject
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * add the given token to the content of this PdfObject
     * @param token the token being added
     * @throws IllegalStateException if this PdfObject is complete
     */
    public void addTokenTo(PdfToken token) {
        if (complete) {
            throw new IllegalStateException();
        }
        content.addToken(token);
    }

    /**
     * change the state of this PdfObject into finished
     */
    public void finish() {
        complete = true;
        if (content.getLexeme(0).equals("<<")) {
            if (content.getLexeme(content.size() - 1).equals(">>")) {
                type = "Dictionary";
            } else if (content.getLexeme(content.size() - 1).equals("endstream")) {
                type = "ContentStream";
            }
        } else if (content.getLexeme(0).equals("[") &&
                   content.getLexeme(content.size() - 1).equals("]")) {
            type = "List";
        } else if (content.size() == 1) {
            type = content.getType(0);
        }
    }

    /**
     * @return the content of stream dictionary as a TokenList
     * @throws IllegalStateException if this PdfObject is not a content stream
     */
    public TokenList getDictPart() {
        if (!type.equals("ContentStream")) {
            throw new IllegalStateException("not a content stream");
        }
        TokenList result = new TokenList();
        for (int i = 0; i < content.size(); i++) {
            if (content.getLexeme(i).equals("stream")) {
                break;
            } else {
                result.addToken(content.getType(i), content.getLexeme(i));
            }
        }
        return result;
    }

    /**
     * @return the type of this PdfObject
     */
    public String getType() {
        return type;
    }

    /**
     * @return the content of this PdfObject
     */
    public TokenList getContent() {
        return content;
    }

    /**
     * @param other the PdfObject being compared to
     * @return a positive number if this PdfObject is ordered before the given other PdfObject;
     *         return a negative number if this PdfObject is ordered after the given other
     *         PdfObject; return 0 if this PdfObject is ordered at the same position as the other
     *         given PdfObject
     */
    public int compareTo(PdfObject other) {
        return this.ID - other.ID;
    }

    /**
     * @return a String representation of this PdfObject
     */
    public String toString() {
        return byteIndex + "_" + generation;
    }
}
