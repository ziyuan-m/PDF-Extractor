import java.io.*;

public class LexicalAnalyzer {

    private boolean isEndOfName(int n) {
        return n == 40 || n == 41 || n == 60 || n == 62 || n == 91 || n == 93 || n == 123 || n == 125 || n == 47 || n == 37;
    }

    public LexicalAnalyzer() {

    }


    public TokenList tokenize(byte[] input) {
        TokenList result = new TokenList();
        boolean isInComment = false;
        boolean isInStream = false;
        boolean isInName = false;
        for (int i = 0; i < input.length; i++) {
            int startIndex = i;
            if (isInStream) {
                for (int j = i; j < input.length; j++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    for (int k = j; k < j + 9; k++) {
                        if (k < input.length) {
                            stream.write(input[k]);
                        }
                    }
                    if (stream.toString().equals("endstream")) {
                        int endIndex = j - 2 ;
                        result.addToken("streamContent", (startIndex) + ":" + endIndex);
                        result.addToken("keyword", "endstream");
                        isInStream = false;
                        i = j + 8;
                        break;
                    }
                }
            } else if (isInComment) {
                StringBuilder commentBuilder = new StringBuilder();
                for (int j = i; j < input.length; j++) {
                    if (input[j] == 10 || input[j] == 13) {
                        isInComment = false;
                        result.addToken("comment", commentBuilder.toString());
                        i = j;
                        break;
                    } else {
                        commentBuilder.append((char) input[j]);
                    }
                }
            } else if (isInName) {
                StringBuilder nameBuilder = new StringBuilder();
                for (int j = i; j < input.length; j++) {
                    if (LexicalResource.isWhiteSpace(input[j]) || isEndOfName(input[j])) {
                        isInName = false;
                        result.addToken("name", nameBuilder.toString());
                        if (isEndOfName(input[j])) {
                            i = j - 1;
                        } else {
                            i = j;
                        }
                        break;
                    } else {
                        nameBuilder.append((char) input[j]);
                    }
                }
            } else {
                if (!LexicalResource.isWhiteSpace(input[i])) {
                    if (input[i] == 37) {
                        isInComment = true;
                    } else if (input[i] == 47) {
                        isInName = true;
                    } else if (input[i] == 60 && i + 1 < input.length && input[i + 1] == 60) {
                        result.addToken("delimiter", "<<");
                        i++;
                    } else if (input[i] == 62 && i + 1 < input.length && input[i + 1] == 62) {
                        result.addToken("delimiter", ">>");
                        i++;
                    } else if (LexicalResource.isDelimiter(input[i])) {
                        result.addToken("delimiter", (char) input[i] + "");
                    } else {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                        String lastEvaluation = "";
                        boolean beenValidBefore = false;
                        for (int j = i; j < input.length; j++) {
                            byteStream.write(input[j]);
                            String inputString = byteStream.toString();
                            if (LexicalResource.isHexString(inputString)) {
                                result.addToken("hexString", inputString);
                                i = j;
                                break;
                            } else if (LexicalResource.isLiteralString(inputString)) {
                                result.addToken("literalString", inputString);
                                i = j;
                                break;
                            } else if (LexicalResource.isKeyword(inputString)) {
                                lastEvaluation = "keyword";
                                beenValidBefore = true;
                            } else if (LexicalResource.isNumber(inputString)) {
                                lastEvaluation = "number";
                                beenValidBefore = true;
                            } else if (LexicalResource.isIdentifier(inputString)) {
                                lastEvaluation = "identifier";
                                beenValidBefore = true;
                            } else if (beenValidBefore && !(lastEvaluation.equals("number") && input[j] == '.')) {
                                StringBuilder lexemeBuilder = new StringBuilder();
                                for (int k = i; k <= j - 1; k++) {
                                    lexemeBuilder.append((char) input[k]);
                                }
                                String lexeme = lexemeBuilder.toString();
                                result.addToken(lastEvaluation, lexeme);
                                if (lexeme.equals("stream")) {
                                    isInStream = true;
                                    int start = j;
                                    byte nextByte = input[start];
                                    while (LexicalResource.isWhiteSpace(nextByte)) {
                                        start++;
                                        nextByte = input[start];
                                    }
                                    i = start - 1;
                                } else {
                                    i = j - 1;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
