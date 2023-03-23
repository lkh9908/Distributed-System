package comp533.reducer;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import comp533.keyValue.KeyValueInterface;


public class Reducer extends AMapReduceTracer implements ReducerInterface<String, Integer>{
	@Override
	public Map<String, Integer> reduce(final List<KeyValueInterface<String, Integer>> argList) {
		System.out.println("****************");
		System.out.println(argList);
        final Map<String, Integer> newMap = new HashMap<>();
        for (KeyValueInterface<String, Integer> keyValue: argList) {
            final String key = keyValue.getKey();
            final int value = keyValue.getValue();
            if (newMap.containsKey(key)) {
                int sum = newMap.get(key);
                sum += value;
                newMap.put(key, sum);
            } else {
            	newMap.put(key, value);
            }
        }
        this.traceReduce(argList, newMap);
        System.out.println("map");
        System.out.println(argList);
        return newMap;
    }

	

}
