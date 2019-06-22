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

    public PdfObject(int byteIndex, int generation, String stateIndicater) {
        this.byteIndex = byteIndex;
        this.generation = generation;
        isInUse = stateIndicater.equals("n");
        type = "notDefined";
    }

    public PdfObject() {
        byteIndex = -1;
        content = new TokenList();
        complete = false;
        type = "notDefined";
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getGeneration() {
        return generation;
    }

    public void addTokenTo(PdfToken token) {
        if (!complete) {
            content.addToken(token);
        }
    }

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

    public String getType() {
        return type;
    }

    public TokenList getContent() {
        return content;
    }

    public int compareTo(PdfObject other) {
        return this.ID - other.ID;
    }

    public String toString() {
        return byteIndex + "_" + generation;
    }
}
