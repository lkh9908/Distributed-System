package comp533.mapper;

import comp533.keyValue.KeyValue;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class SumMapper extends AMapReduceTracer implements MapperInterface<String, Integer> {
	final String resultKey = "Sum";
	
	@Override
    public String toString() {
    	return AMapReduceTracer.INT_SUMMING_MAPPER ;
    }

	@Override
    public KeyValue<String, Integer> map(final String stringArg) {
        final KeyValue<String, Integer> keyValue = new KeyValue<>(resultKey, Integer.parseInt(stringArg));
        this.traceMap(stringArg, keyValue);
        return keyValue;
    }
}
