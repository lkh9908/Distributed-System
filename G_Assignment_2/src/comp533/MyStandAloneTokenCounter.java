package comp533;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class MyStandAloneTokenCounter extends AMapReduceTracer {
	final static String QUIT = "quit";
	final static Scanner SCAN = new Scanner(System.in);
	public static void main(final String[] args) {
		final MyStandAloneTokenCounter counter = new MyStandAloneTokenCounter();
        while (true)  {
//        	System.out.println("Please enter quit or a line of tokens to be processed separated by spaces");
        	counter.traceNumbersPrompt();
        	
            final String line = SCAN.nextLine();
            if (QUIT.equals(line)) {
            	SCAN.close();
                return;
            }
            
            final String[] tokens = line.split(" ");
            
            final Map<String, Integer> tokenMap = new HashMap<String, Integer>();
            for (String token: tokens) {
                if (tokenMap.get(token) == null) {
                	tokenMap.put(token, 1);
                } else {
                    final int count = tokenMap.get(token);
                    tokenMap.put(token, count + 1);
                }
            }
            System.out.println("Output:");
            final String result = tokenMap.toString();
            System.out.println(result.substring(1, result.length() - 1));
        }
    }
}
