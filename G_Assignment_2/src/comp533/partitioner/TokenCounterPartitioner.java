package comp533.partitioner;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class TokenCounterPartitioner extends AMapReduceTracer implements Partitioner<String, Integer> {
    @Override
    public int getPartition(final String key, final Integer value, final int numOfPartitions) {
    	char keyFirstChar = key.charAt(0);
        if (!Character.isLetter(keyFirstChar)) {
            return 0;
        }
        char lowercaseKeyFirstChar = Character.toLowerCase(keyFirstChar);
        int maxPartitionSize = (int) Math.ceil((('z' - 'a' + 1) * 1.0) / (numOfPartitions * 1.0));
        return (int) Math.floor(((lowercaseKeyFirstChar - 'a' + 1) * 1.0) / (maxPartitionSize * 1.0));
    }
    
    @Override
    public String toString() {
        return AMapReduceTracer.PARTITIONER;
    }
}
