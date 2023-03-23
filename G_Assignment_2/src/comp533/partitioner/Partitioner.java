package comp533.partitioner;

public interface Partitioner<K,V> {
    public abstract int getPartition(K key, V value, int numOfPartitions);
}
