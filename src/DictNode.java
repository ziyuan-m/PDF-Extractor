/**
 * DictNode represents an element in a PDF DICTIONARY.
 * If nextLevel is null, it represents a terminal node that does not have nested DICTIONARY.
 * In this case, it is a NAME, a String, or a ARRAY.
 */
public class DictNode {
    String content;
    DictNode[] next;

    public DictNode(String content, DictNode[] nextLevel) {
        this.content = content;
        this.next = nextLevel;
    }
}
