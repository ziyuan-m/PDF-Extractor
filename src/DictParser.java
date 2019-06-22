public class DictParser {
    private int index;
    private TokenList inputTokens;

    public DictParser(TokenList inputTokens) {
        this.inputTokens = inputTokens;
    }

    public boolean dict() {
        int save = index;
        if (dict1()) {
            return true;
        } else {
            index = save;
        }

        save = index;
        if (dict2()) {
            return true;
        } else {
            index = save;
        }

        return false;
    }

    private boolean nextTypeIs(String type) {
        return inputTokens.getType(index++).equals(type);
    }

    private boolean nextLexemeIs(String lexeme) {
        return inputTokens.getLexeme(index++).equals(lexeme);
    }

    private boolean keyValuePair1() {
        return nextTypeIs("name") && nextTypeIs("name");
    }

    private boolean keyValuePair2() {
        return nextTypeIs("name") && nextTypeIs("number") && nextTypeIs("number") && nextLexemeIs("R");
    }

    private boolean keyValuePair3() {
        return nextTypeIs("name") && dict();
    }

    private boolean keyValuePair() {
        int save = index;
        if (keyValuePair1()) {
            return true;
        } else {
            index = save;
        }
        save = index;
        if (keyValuePair2()) {
            return true;
        } else {
            index = save;
        }

        save = index;
        if (keyValuePair3()) {
            return true;
        } else {
            index = save;
        }

        return false;
    }

    private boolean content1() {
        return keyValuePair();
    }

    private boolean content2() {
        return keyValuePair() && content();
    }

    private boolean content() {
        int save = index;
        if (content1()) {
            return true;
        } else {
            index = save;
        }

        save = index;
        if (content2()) {
            return true;
        } else {
            index = save;
        }

        return false;
    }

    private boolean dict1() {
        return nextLexemeIs("<<") && nextLexemeIs(">>");
    }

    private boolean dict2() {
        return nextLexemeIs("<<") && content() && nextLexemeIs(">>");
    }
}
