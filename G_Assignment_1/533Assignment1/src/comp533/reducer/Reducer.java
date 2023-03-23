package comp533.reducer;

import comp533.keyValue.KeyValue;
import comp533.keyValue.KeyValueInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Reducer extends AMapReduceTracer implements ReducerInterface<String, Integer>{
    public Map<String, Integer> reduce(final List<KeyValue<String, Integer>> argList) {
        final Map<String, Integer> map = new HashMap<>();
        for (KeyValueInterface<String, Integer> keyValue: argList) {
            final String key = keyValue.getKey();
            final Integer value = keyValue.getValue();
            if (map.containsKey(key)) {
                Integer sum = map.get(key);
                sum += value;
                map.put(key, sum);
            } else {
                map.put(key, value);
            }
        }
        this.traceReduce(argList, map);
        return map;
    }
}