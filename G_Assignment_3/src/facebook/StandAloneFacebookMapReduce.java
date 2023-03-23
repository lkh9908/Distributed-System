package facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class StandAloneFacebookMapReduce extends AMapReduceTracer {
	final static String QUIT = "quit";
	final static Scanner SCAN = new Scanner(System.in);
	public static void main(final String[] args) {
		final StandAloneFacebookMapReduce counter = new StandAloneFacebookMapReduce();
        while (true)  {
        	System.out.println("Please enter quit or a line of tokens to be processed separated by spaces");
        	counter.traceNumbersPrompt();
        	
            final String line = SCAN.nextLine();
            if (QUIT.equals(line)) {
            	SCAN.close();
                return;
            }
            
            final String[] tokens = line.split(" ");
            
            final Map<String, List<String>> tokenMap = new HashMap<String, List<String>>();
            for (String token: tokens) {
            	String[] all = token.split(",");
            	String user = all[0];
            	String[] friends = Arrays.copyOfRange(all, 1, all.length);
            	for (String friend: friends) {
            		List<String> value = Arrays.asList(friends);
        			String key = buildKey(user, friend);
        			System.out.println(key +" " + value);
        			
            		if (tokenMap.get(key) == null) {
            			tokenMap.put(key, value);
                    } else {
                        final List<String> current = tokenMap.get(key);
                        
						final List<String> newValue = intersection(current, value);
                        tokenMap.put(key, newValue);
                    }
            		
        			
        			
            	}
//            	System.out.println("");
                
            }
            
            
            System.out.println("Output:");
            final String result = tokenMap.toString();
            System.out.println(result.substring(1, result.length() - 1));
        }
    }
	
	public static String buildKey(String user, String friend) {
		if (user.compareTo(friend) < 0) {
			return user+"_"+friend;
		} else {
			return friend+"_"+user;
		}
		
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
