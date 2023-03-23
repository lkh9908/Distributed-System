package comp533.mapper;

import java.util.ArrayList;
import java.util.List;

import comp533.keyValue.KeyValue;
import comp533.keyValue.KeyValueInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class Mapper<K,V> extends AMapReduceTracer implements MapperInterface<String, Integer> {
//	private List<String> tokenList;


//    public Mapper(final String[] tokens) {
//        for (String token: tokens) {
//        	tokenList.add(token);
//        }
//    }
//
//    public List<String> getTokenSet() {
//        return tokenList;
//    }
//
//    public void printMapper() {
//        for (String token: this.tokenList) {
//            this.traceMap(token, new KeyValue<String, Integer>(token, 1));
//        }
//    }


	@Override
	public List<KeyValue<String, Integer>> map(final List<String> aStrings) {
		final List<KeyValue<String, Integer>> keyValueList = new ArrayList<KeyValue<String, Integer>>(); 
//		System.out.println("ssss");
//		System.out.println(aStrings);
		for (String token: aStrings) {
			final KeyValueInterface<String, Integer> current;
			current = new KeyValue<String, Integer>(token, 1);
//			System.out.println(current);
//			System.out.println(keyValueList);
			keyValueList.add((KeyValue<String, Integer>) current);
        }
		this.traceMap(aStrings, keyValueList);
		return keyValueList;
	}
	
	
	@Override
    public String toString() {
    	return AMapReduceTracer.TOKEN_COUNTING_MAPPER;
    }
}
