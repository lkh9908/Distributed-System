package comp533.mapper;

import comp533.keyValue.KeyValueInterface;
import comp533.keyValue.KeyValue;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class Mapper extends AMapReduceTracer implements MapperInterface<String, Integer>{
    @Override
    public KeyValueInterface<String, Integer> map(String stringArg) {
        KeyValueInterface<String, Integer> keyValue = new KeyValue<>(stringArg, 1);
        this.traceMap(stringArg, keyValue);
        return keyValue;
    }
}
