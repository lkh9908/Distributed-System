package comp533.keyValue;

import java.io.Serializable;

public interface KeyValueInterface<K, V> extends Serializable {
    K getKey();
    V getValue();
}
