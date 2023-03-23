package comp533;
//
//import gradingTools.comp533s19.assignment0.AMapReduceTracer;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//public class TokenCounter extends AMapReduceTracer {
//    public static void main(String[] args) {
//        TokenCounter counter = new TokenCounter();
//        Scanner inputHandler = new Scanner(System.in);
//        while (true)  {
//            counter.traceNumbersPrompt();
//            String line = inputHandler.nextLine();
//            if (line.equals(AMapReduceTracer.QUIT)) {
//            	inputHandler.close();
//                return;
//            }
//            String[] tokens = line.split(" ");
//            Map<String, Integer> result = new HashMap<String, Integer>();
//            for (String token: tokens) {
//                if (result.get(token) == null) {
//                    result.put(token, 1);
//                } else {
//                    int value = result.get(token);
//                    result.put(token, value + 1);
//                }
//            }
//            String resultOutput = result.toString();
//            System.out.println(resultOutput.substring(1, resultOutput.length() - 1));
//        }
//        
//    }
//}
import comp533.barrier.Barrier;
import comp533.joiner.Joiner;
import comp533.keyValue.KeyValue;
import comp533.view.TokenCounterView;
import util.models.PropertyListenerRegisterer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface TokenCounter extends PropertyListenerRegisterer {
    String COUNTER_NAME = "Counter";
    Map<String, Integer> getResult();
    ArrayBlockingQueue<KeyValue<String, Integer>> getBoundedBuffer();
    ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueList();
    Barrier getBarrier();
    Joiner getJoiner();
    void interruptThreads();
    void setInputString(String newInputString, TokenCounterView view);
    void updateThreads();
    void setNumThreads(int numThreads, TokenCounterView view);
}