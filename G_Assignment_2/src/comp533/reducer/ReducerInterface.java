package comp533.reducer;

import comp533.keyValue.KeyValue;

import java.util.List;
import java.util.Map;

public interface ReducerInterface<K, V> {
    public Map<K, V> reduce(List<KeyValue<K, V>> argList);
}
