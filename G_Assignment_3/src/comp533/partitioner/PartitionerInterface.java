package comp533.partitioner;

public interface PartitionerInterface<K,V> {
    int getPartition(K key, V value, int numOfPartitions);
}
