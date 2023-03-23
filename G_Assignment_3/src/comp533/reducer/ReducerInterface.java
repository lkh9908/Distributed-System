package comp533.reducer;

import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValueInterface;

public interface ReducerInterface<K, V> {
    public Map<K, V> reduce(List<KeyValueInterface<K, V>> argList);
}
