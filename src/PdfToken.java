/**
 * Represents a token of PDF codes
 */
public class PdfToken {
    public String type;
    public String lexeme;

    /**
     * Constructs a PdfToken with given type and lexeme
     * @param type is the type of PdfToken being constructed
     * @param lexeme is the lexeme of PdfToken being constructed
     */
    public PdfToken(String type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }
}
