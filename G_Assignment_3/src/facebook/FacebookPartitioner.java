package facebook;

import java.util.List;

import comp533.partitioner.PartitionerInterface;

//import util.trace.Tracer;

public class FacebookPartitioner implements PartitionerInterface<String, List<String>> {

    public int getPartition(String key, List<String> value, int numOfPartitions) {
        char keyFirstChar = key.charAt(0);
        if (!Character.isLetter(keyFirstChar)) {
            return 0;
        }
        char lowercaseKeyFirstChar = Character.toLowerCase(keyFirstChar);
        int maxPartitionSize = (int) Math.ceil((('z' - 'a' + 1) * 1.0) / (numOfPartitions * 1.0));
        return (int) Math.floor(((lowercaseKeyFirstChar - 'a' + 1) * 1.0) / (maxPartitionSize * 1.0));
    }
}
