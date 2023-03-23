package comp533.mapper;

import java.util.ArrayList;
import java.util.List;

import comp533.keyValue.KeyValue;
import comp533.keyValue.KeyValueInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class SumMapper<K, V> extends AMapReduceTracer implements MapperInterface<String, Integer> {
	final String resultKey = "ResultKey";
	@Override
	public List<KeyValue<String, Integer>> map(final List<String> aStrings) {
		final List<KeyValue<String, Integer>> keyValueList = new ArrayList<KeyValue<String, Integer>>(); 

		for (String token: aStrings) {
			
			final KeyValueInterface<String, Integer> current = new KeyValue<String, Integer>(resultKey, Integer.parseInt(token));

			keyValueList.add((KeyValue<String, Integer>) current);
        }
		this.traceMap(aStrings, keyValueList);
		return keyValueList;
	}
	
	
	@Override
    public String toString() {
    	return AMapReduceTracer.INT_SUMMING_MAPPER ;
    }

}
