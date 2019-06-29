import java.util.Stack;

/**
 * Represents a Tree-ish structure that has arbitrary number of branches at each level
 */
public class StructureTree {
    public static final int DEFAULT_DICT_CAPACITY = 50;
    public DictNode[] root;
    private Stack<Integer> indexRecord;
    private int currentIndex;
    private DictNode[] currentDict;

    /**
     * Constructs a StructureTree
     */
    public StructureTree() {
        root = new DictNode[DEFAULT_DICT_CAPACITY];
        root[0] = new DictNode("Parent", root);
        indexRecord = new Stack<>();
        currentIndex = 1;
        currentDict = root;
    }

    /**
     * Constructs a StructureTree using the given root
     * @param root is a root of a StructureTree
     */
    public StructureTree(DictNode[] root) {
        this.root = root;
        root[0] = new DictNode("Parent", root);
        indexRecord = new Stack<>();
        currentIndex = 1;
        currentDict = root;
    }

    /**
     * Adds a key/value pair, whose value is not a dictionary, to this SturctureTree
     * @param key is the key of the key/value pair being added
     * @param content is the content of the key/value pair being added
     */
    public void addNonDictValue(String key, String content) {
        checkCapacity();
        currentDict[currentIndex++] = new DictNode(key + "=" + content, null);
    }

    /**
     * Closes the writing process of the dictionary on current level
     */
    public void closeCurrentDict() {
        if (currentDict != root) {
            currentDict = currentDict[0].next;
            currentIndex = indexRecord.pop();
        }
    }

    /**
     * Adds a key/value pair, whose value is a dictionary, to this StructureTree
     * @param key is the key of the key/value pair being added
     * @param content is the content of the key/value pair being added
     */
    public void addNestedDict(String key, String content) {
        checkCapacity();
        DictNode[] nextLevel = new DictNode[DEFAULT_DICT_CAPACITY];
        currentDict[currentIndex++] = new DictNode(key + "=" + content, nextLevel);
        indexRecord.push(currentIndex);
        nextLevel[0] = new DictNode("Previous Level", currentDict);
        currentDict = nextLevel;
        currentIndex = 1;
    }

    /**
     * @return a String representation of this StructureTree
     */
    public String toString() {
        return toStringHelper(1, root);
    }

    private String toStringHelper(int level, DictNode[] dict) {
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

    /**
     * Changes the state of this StructureTree to finished
     */
    public void finish() {
        resetPointer();
    }

    /**
     * @return the root of this StructureTree
     */
    public DictNode[] getRoot() {
        return root;
    }

    /**
     * @return the current dictionary
     */
    public DictNode[] getCurrentDict() {
        return currentDict;
    }

    /**
     * Sets the pointer to the next key/value pair
     */
    public void goToNext() {
        currentIndex++;
    }

    /**
     * Set the pointer to the first key/value pair of the dictionary in the next level of the
     * next key/value pair
     * @throws IllegalStateException if the value of next key/value pair is not a dictionary
     */
    public void goToNextDict() {
        if (currentDict[currentIndex].next == null) {
            throw new IllegalStateException("next is not dict");
        }
        currentDict = currentDict[currentIndex].next;
        currentIndex = 1;
    }

    /**
     * @return {@code true} if the value of next key/value pair is a dictionary
     */
    public boolean nextValueIsDict() {
        return currentDict[currentIndex].next != null;
    }

    /**
     * @return the non-dictionary value of next key/value pair
     * @throws IllegalStateException if next value is a dictionary
     */
    public String nextNonDictValue() {
        if (nextValueIsDict()) {
            throw new IllegalStateException("next value is dictionary");
        }
        return currentDict[currentIndex].content;
    }

    /**
     * @param key is the key of the required value
     * @return the value of the key/value pair indicated by the given key
     */
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

    /**
     * Set the pointer to the first key/value pair of the dictionary indicated by the given key
     * @param key is the key of the required dictionary value
     */
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

    /**
     * @return the key of the next key/value pair
     */
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

    /**
     * Resets the pointer to the first key/value pair in the first level
     */
    public void resetPointer() {
        currentDict = root;
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
}
