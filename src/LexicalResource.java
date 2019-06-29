import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains the resources for lexical analysis
 */
public class LexicalResource {
    static final String NUM_REGEX = "[+\\-]?\\d+(\\.\\d+)?(E[+\\-]?\\d+)?";
    static final String KEYWORD_REGEX = "stream|endstream|true|false|f|n|starxref" +
                                            "|xref|trailer|null|obj|endobj";
    static final String ID_REGEX = "([a-zA-Z]([a-zA-Z]|\\d)*)([+\\-]?\\d+(\\.\\d+)?(E[+\\-]?\\d+)?)?";
    static final String STRING_REGEX = "\\((.|\\p{Space})*\\)|(<([a-zA-Z]|\\d)*>)";
    static final String NAME_REGEX = "/" + ID_REGEX;
    static final String HEX_REGEX = "([a-fA-F]|\\d)*";
    static final int[] WITHE_SPACE_LIST = {0, 9, 10, 12, 13, 32};
    static final int[] DELIMITER_LIST = {91, 93, 123, 125};
    static final byte[] STREAM_BYTE_ARRAY = {115, 116, 114, 101, 97, 109};

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a white space
     */
    public static boolean isWhiteSpace(byte input) {
        return Arrays.binarySearch(WITHE_SPACE_LIST, input) >= 0;
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a delimiter
     */
    public static boolean isDelimiter(byte input) {
        return Arrays.binarySearch(DELIMITER_LIST, input) >= 0;
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a literal String
     */
    public static boolean isLiteralString(String input) {
        if (input.charAt(0) != '(' | input.charAt(input.length() - 1) != ')') {
            return false;
        }
        int unbalancedNum = 0;
        boolean inEscape = false;
        for (int i = 0; i < input.length(); i++) {
            if (!inEscape) {
                if (input.charAt(i) == '\\') {
                    inEscape = true;
                } else if (input.charAt(i) == '(') {
                    unbalancedNum++;
                } else if (input.charAt(i) == ')') {
                    unbalancedNum--;
                }
            } else {
                inEscape = false;
            }
        }
        return unbalancedNum == 0;
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a Hex String
     */
    public static boolean isHexString(String input) {
        if (input.charAt(0) != '<' || input.charAt(input.length() - 1) != '>') {
            return false;
        } else if (input.length() == 2) {
            return true;
        } else {
            for (int i = 1; i < input.length() - 1; i++) {
                if (!Character.isDigit(input.charAt(i)) && !Character.isAlphabetic(input.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a keyword
     */
    public static boolean isKeyword(String input) {
        Pattern keywordPattern = Pattern.compile(KEYWORD_REGEX);
        Matcher keywordMatcher = keywordPattern.matcher(input);
        return keywordMatcher.matches();
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a number
     */
    public static boolean isNumber(String input) {
        Pattern numberPattern = Pattern.compile(NUM_REGEX);
        Matcher numMatcher = numberPattern.matcher(input);
        return numMatcher.matches();
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a identifier
     */
    public static boolean isIdentifier(String input) {
        Pattern identifierPattern = Pattern.compile(ID_REGEX);
        Matcher numMatcher = identifierPattern.matcher(input);
        return numMatcher.matches();
    }

    /**
     * @param input the input to be evaluated
     * @return {@code true} if the given input is a name
     */
    public static boolean isName(String input) {
        Pattern namePattern = Pattern.compile(NAME_REGEX);
        Matcher nameMatcher = namePattern.matcher(input);
        return nameMatcher.matches();
    }
}
