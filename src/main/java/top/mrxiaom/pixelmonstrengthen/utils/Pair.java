package top.mrxiaom.pixelmonstrengthen.utils;

public class Pair<Key, Value> {
    Key key;
    Value value;

    private Pair(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public static <Key, Value> Pair<Key, Value> of(Key key, Value value) {
        return new Pair<>(key, value);
    }
}
