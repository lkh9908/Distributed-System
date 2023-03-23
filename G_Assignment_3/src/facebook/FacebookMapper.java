package facebook;

import comp533.keyValue.KeyValueInterface;
import comp533.mapper.MapperInterface;

import java.util.Arrays;
import java.util.List;

import comp533.keyValue.KeyValue;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class FacebookMapper extends AMapReduceTracer implements MapperInterface<String, List<String>> {
//	final String resultKey = "Sum";
	
//	@Override
//    public String toString() {
//    	return AMapReduceTracer. ;
//    }

	public KeyValueInterface<String, List<String>> map(final String token, final String user, String friend, String[] friends) {

		List<String> value = Arrays.asList(friends);
		String key = buildKey(user, friend);
		System.out.println(key +" " + value);
		

    	final KeyValueInterface<String, List<String>> keyValue = new KeyValue<>(key, value);
    	
    	this.traceMap(token, keyValue);
        return keyValue;
    }
	
	public static String buildKey(String user, String friend) {
		if (user.compareTo(friend) < 0) {
			return user+"_"+friend;
		} else {
			return friend+"_"+user;
		}
		
	}

	@Override
	public KeyValueInterface<String, List<String>> map(String stringArg) {
		// TODO Auto-generated method stub
		return null;
	}
}



