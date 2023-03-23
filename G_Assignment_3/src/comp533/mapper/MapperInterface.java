package comp533.mapper;

import comp533.keyValue.KeyValueInterface;

public interface MapperInterface<K, V> {
    public KeyValueInterface<K, V> map(String stringArg);
}
