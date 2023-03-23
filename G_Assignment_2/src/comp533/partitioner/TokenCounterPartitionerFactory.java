package comp533.partitioner;

public class TokenCounterPartitionerFactory {
    private static TokenCounterPartitioner partitioner;

    static {
        partitioner = new TokenCounterPartitioner();
    }

    public static TokenCounterPartitioner getPartitioner() {
        return TokenCounterPartitionerFactory.partitioner;
    }
    
    public static void setPartitioner(final TokenCounterPartitioner newPartitioner) {
        partitioner = newPartitioner;
    }
}
