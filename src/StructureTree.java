import java.util.Stack;

public class StructureTree {
    public static final int DEFAULT_DICT_CAPACITY = 50;
    public DictNode[] root;
    private Stack<Integer> indexRecord;
    private int currentIndex;
    private DictNode[] currentDict;


    public StructureTree() {
        root = new DictNode[DEFAULT_DICT_CAPACITY];
        root[0] = new DictNode("Parent", root);
        indexRecord = new Stack<>();
        currentIndex = 1;
        currentDict = root;
    }

    public StructureTree(DictNode[] root) {
        this.root = root;
        root[0] = new DictNode("Parent", root);
        indexRecord = new Stack<>();
        currentIndex = 1;
        currentDict = root;
    }

    public void addNonDictValue(String key, String content) {
        checkCapacity();
        currentDict[currentIndex++] = new DictNode(key + "=" + content, null);
    }

    public void closeCurrentDict() {
        if (currentDict != root) {
            currentDict = currentDict[0].next;
            currentIndex = indexRecord.pop();
        }
    }

    public void addNestedDict(String key, String content) {
        checkCapacity();
        DictNode[] nextLevel = new DictNode[DEFAULT_DICT_CAPACITY];
        currentDict[currentIndex++] = new DictNode(key + "=" + content, nextLevel);
        indexRecord.push(currentIndex);
        nextLevel[0] = new DictNode("Previous Level", currentDict);
        currentDict = nextLevel;
        currentIndex = 1;
    }

    private boolean checkCapacity() {
        if (currentIndex >= currentDict.length) {
            DictNode[] update = new DictNode[currentDict.length * 2];
            for (int i = 0; i < currentDict.length; i++) {
                update[i] = currentDict[i];
            }
            currentDict = update;
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return toStringHelper(1, root);
    }

    public String toStringHelper(int level, DictNode[] dict) {
        String result = "{" + level + "}--->";
        DictNode currNode = dict[1];
        int index = 1;
        while (currNode != null) {
            result += currNode.content;
            if (currNode.next != null) {
                result += "=" + toStringHelper(level + 1, currNode.next);
            } else {
                result += " | ";
            }
            index++;
            currNode = dict[index];
        }
        result += "<---{" + level + "}";
        return result;
    }

    public void finish() {
        resetPointer();
    }

    public DictNode[] getRoot() {
        return root;
    }

    public DictNode[] getCurrentDict() {
        return currentDict;
    }

    public void goToNext() {
        currentIndex++;
    }

    public void goToNextDict() {
        if (currentDict[currentIndex].next == null) {
            throw new IllegalStateException("next is not dict");
        }
        currentDict = currentDict[currentIndex].next;
        currentIndex = 1;
    }

    public boolean nextValueIsDict() {
        return currentDict[currentIndex].next != null;
    }

    public String nextNonDictValue() {
        if (nextValueIsDict()) {
            throw new IllegalStateException("next value is dictionary");
        }
        return currentDict[currentIndex].content;
    }

    public String valueOf(String key) {
        String currKey = currentDict[1].content.split("=")[0];
        int index = 2;
        while (!currKey.equals(key) && index < currentDict.length) {
            if (currentDict[index] == null) {
                break;
            }
            currKey = currentDict[index].content.split("=")[0];
            index++;
        }
        if (index >= currentDict.length) {
            throw new IllegalArgumentException("given key does not exist");
        }
        return currentDict[index - 1].content.split("=")[1];
    }

    public void goToDictWithKey(String key) {
        String currKey = currentDict[1].content.split("=")[0];
        int index = 2;
        while (!currKey.equals(key) && index < currentDict.length) {
            if (currentDict[index] == null) {
                break;
            }
            currKey = currentDict[index].content.split("=")[0];
            index++;
        }
        if (index >= currentDict.length) {
            throw new IllegalArgumentException("given key does not exist");
        }
        currentDict = currentDict[index - 1].next;
        currentIndex = 1;
    }

    public String nextKey() {
        if (currentIndex >= currentDict.length) {
            throw new IllegalStateException("no next element");
        }
        if (currentDict[currentIndex].next == null) {
            return currentDict[currentIndex].content.split("=")[0];
        } else {
            return currentDict[currentIndex].content;
        }
    }

    public void resetPointer() {
        currentDict = root;
        currentIndex = 1;
    }
}
