package comp533.mapper;

import java.util.List;

import comp533.keyValue.KeyValue;

public interface MapperInterface<K, V> {
	List<KeyValue<K, V>> map(List<String> aStrings) ;
}
