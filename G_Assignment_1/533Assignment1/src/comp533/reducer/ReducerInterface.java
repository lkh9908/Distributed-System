package comp533.reducer;
import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValue;

public interface ReducerInterface<K, V> {
    public Map<K, V> reduce(List<KeyValue<String, Integer>> resultMap);
}

