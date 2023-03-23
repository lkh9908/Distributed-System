package facebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValueInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class FacebookReducer extends AMapReduceTracer{
	public Map<String, List<String>> reduce(final List<KeyValueInterface<String, List<String>>> friends) {
		System.out.println("****************");
		System.out.println(friends);
        final Map<String, List<String>> newMap = new HashMap<>();
        for (KeyValueInterface<String, List<String>> keyValue: friends) {
            final String key = keyValue.getKey();
            final List<String> value = keyValue.getValue();

            if (newMap.get(key) == null) {
            	newMap.put(key, value);
            } else {
                final List<String> current = newMap.get(key);
                
				final List<String> newValue = intersection(current, value);
				newMap.put(key, newValue);
            }
        }
        this.traceReduce(friends, newMap);
        System.out.println("reducer11111111111111111111111111111111111111");
    	this.traceRemoteList(friends);
    	System.out.println("reducer2222222222222222222222222222222222222222");
        System.out.println("map");
        System.out.println(friends);
        return newMap;
    }
	
	
	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
