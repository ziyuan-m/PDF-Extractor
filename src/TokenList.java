public class TokenList {
    public static final int DEFAULT_CAPACITY = 1000;

    private PdfToken[] elementData;
    private int size;


    public TokenList() {
        elementData = new PdfToken[DEFAULT_CAPACITY];
    }

    // post: return a new TokenList that contains the token from index 'start' to 'end' of
    //       this TokenList
    public TokenList subList(int start, int end) {
        TokenList result = new TokenList();
        for (int i = start; i < end; i++) {
            result.addToken(getType(i), getLexeme(i));
        }
        return result;
    }

    public void addToken(String type, String lexeme) {
        checkCapacity(size);
        PdfToken token = new PdfToken(type, lexeme);
        elementData[size] = token;
        size++;
    }

    public void addToken(PdfToken token) {
        checkCapacity(size);
        elementData[size] = token;
        size++;
    }

    public void set(String type, String lexeme, int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
        elementData[index] = new PdfToken(type, lexeme);
    }

    public int size() {
        return size;
    }

    public PdfToken getToken(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
        return elementData[index];
    }

    public String getType(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index + ", size: " + size);
        }
        return elementData[index].type;
    }

    public String getLexeme(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index + ", size: " + size);
        }
        return elementData[index].lexeme;
    }

    public String toString() {
        String result = "[(" + elementData[0].type + ", " + elementData[0].lexeme + ")";
        for (int i = 1; i < size; i++) {
            result += "; (" + elementData[i].type + ", " + elementData[i].lexeme + ")";
        }
        result += "]";
        return result;
    }

    public boolean checkCapacity(int index) {
        if (index >= elementData.length) {
            PdfToken[] update = new PdfToken[elementData.length * 2];
            for (int i = 0; i < elementData.length; i++) {
                update[i] = elementData[i];
            }
            elementData = update;
            return true;
        } else {
            return false;
        }
    }
}
