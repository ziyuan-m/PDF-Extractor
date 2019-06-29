import java.util.Arrays;

/**
 * This class contains methods for analyzing the content stream
 */
public class ContentStreamAnalyzer {
    public static final String[] OPERATORS = {"\"", "'", "BT", "Do", "ET", "T*", "TD", "TJ", "TL", "Tc", "Td",
                                              "Tf", "Th", "Tj", "Tm", "Tr", "Ts", "Tw", "Tz", "cm"};

    /**
     * Tokenize the binary data of a content stream
     * @param input is the binary data representing the content stream
     * @return a list of tokens transformed from the given binary data
     */
    public static TokenList tokenize(byte[] input) {
        TokenList result = new TokenList();
        String currString = "";
        for (int i = 0; i < input.length; i++) {
            char currCharacter = (char) input[i];
            if (LexicalResource.isWhiteSpace(input[i])) {
                currString = "";
            } else if (currCharacter == '(') {
                StringBuilder stringContent = new StringBuilder();
                for (int j = i + 1; j < input.length; j++) {
                    if ((char) input[j] == ')') {
                        int index = j - 1;
                        int countBackslash = 0;
                        while (index > 0 && input[index] == '\\') {
                            index--;
                            countBackslash++;
                        }
                        if (countBackslash % 2 == 0 && (countBackslash > 1 || countBackslash == 0)) {
                            i = j;
                            break;
                        }
                    } else {
                        stringContent.append((char) input[j]);
                    }
                }
                result.addToken("string", stringContent.toString());
            } else if (currCharacter == '<') {
                if ((char) input[i + 1] == '<') {
                    i = i + 1;
                } else {
                    StringBuilder hexBuilder = new StringBuilder();
                    for (int j = i + 1; j < input.length; j++) {
                        if ((char) input[j] == '>') {
                            i = j;
                            break;
                        } else {
                            hexBuilder.append((char) input[j]);
                        }
                    }
                    String hexString = hexBuilder.toString();
                    StringBuilder literalBuilder = new StringBuilder();
                    for (int j = 0; j < hexString.length() - 1; j += 2) {
                        literalBuilder.append((char) (byte) ((Character.digit(hexString.charAt(j), 16) << 4) +
                                              Character.digit(hexString.charAt(j + 1), 16)));
                    }
                    result.addToken("string", literalBuilder.toString());
                }
            } else if (currCharacter == '[' || currCharacter == ']') {
                result.addToken("delimiter", Character.toString(currCharacter));
            } else if (Character.isDigit(currCharacter)) {
                currString = Character.toString(currCharacter);
                for (int j = i + 1; j < input.length; j++) {
                    currString += (char) input[j];
                    if (!LexicalResource.isNumber(currString) && (char) input[j] != '.') {
                        result.addToken("number", currString.substring(0, currString.length() - 1));
                        currString = "";
                        i = j - 1;
                        break;
                    }
                }
            } else if (currCharacter == '/') {
                StringBuilder name = new StringBuilder();
                for (int j = i + 1; j < input.length; j++) {
                    if ((char) input[j] == ' ') {
                        i = j;
                        break;
                    } else {
                        name.append((char) input[j]);
                    }
                }
                result.addToken("name", name.toString());
            } else {
                currString += (char) input[i];
                if (currString.length() <= 2) {
                    if (Arrays.binarySearch(OPERATORS, currString) >= 0) {
                        result.addToken("keyword", currString);
                        currString = "";
                    }
                } else {
                    currString = "";
                }
            }
        }
        return result;
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }
}
