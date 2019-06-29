/**
 * This class represent a list of tokens.
 */
public class TokenList {
    public static final int DEFAULT_CAPACITY = 1000;

    private PdfToken[] elementData;
    private int size;

    /**
     * Constructs a new TokenList with default capacity
     */
    public TokenList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a new TokenList with the given capacity
     * @param capacity the capacity of the TokenList to be constructed
     */
    public TokenList(int capacity) {
        elementData = new PdfToken[capacity];
    }

    /**
     * @param start starting index
     * @param end ending index
     * @return a new TokenList that contains the token from index 'start' to 'end' of
     *         this TokenList
     */
    public TokenList subList(int start, int end) {
        TokenList result = new TokenList();
        for (int i = start; i < end; i++) {
            result.addToken(getType(i), getLexeme(i));
        }
        return result;
    }

    /**
     * Add a token with the given type and lexeme to this TokenList
     * @param type the type of the token to be added
     * @param lexeme the lexeme of the token to be added
     */
    public void addToken(String type, String lexeme) {
        checkCapacity(size);
        PdfToken token = new PdfToken(type, lexeme);
        elementData[size] = token;
        size++;
    }

    /**
     * Add the given token to this TokenList
     * @param token the token to be added
     */
    public void addToken(PdfToken token) {
        checkCapacity(size);
        elementData[size] = token;
        size++;
    }

    /**
     * Sets the token at given index to be the token with given type and lexeme
     * @param type the type of the token to be set to
     * @param lexeme the lexeme of the token to be set to
     * @param index the index to be set
     */
    public void set(String type, String lexeme, int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
        elementData[index] = new PdfToken(type, lexeme);
    }

    /**
     * @return the size of this TokenList
     */
    public int size() {
        return size;
    }

    /**
     * @param index is the index of the token required
     * @return the token at given index
     */
    public PdfToken getToken(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index);
        }
        return elementData[index];
    }

    /**
     * @param index the index of the token required
     * @return the type of the token at given index
     */
    public String getType(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index + ", size: " + size);
        }
        return elementData[index].type;
    }

    /**
     * @param index the index of the token required
     * @return the lexeme of the token at given index
     */
    public String getLexeme(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("invalid index: " + index + ", size: " + size);
        }
        return elementData[index].lexeme;
    }

    /**
     * @return a String representation of this TokenList
     */
    public String toString() {
        String result = "[(" + elementData[0].type + ", " + elementData[0].lexeme + ")";
        for (int i = 1; i < size; i++) {
            result += "; (" + elementData[i].type + ", " + elementData[i].lexeme + ")";
        }
        result += "]";
        return result;
    }

    private boolean checkCapacity(int index) {
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
