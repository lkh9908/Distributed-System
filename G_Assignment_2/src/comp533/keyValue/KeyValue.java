package comp533.keyValue;

public class KeyValue<K,V> implements KeyValueInterface <K, V> {
	private K key;
    private V value;

    public KeyValue(final K thisKey, final V thisValue) {
        this.key = thisKey;
        this.value = thisValue;
    }

	public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
    
    public void setValue(final V newValue) {
    	this.value = newValue;
    }

    public String toString() {
        return "(" + this.key + "," + this.value + ")";
    }

}
