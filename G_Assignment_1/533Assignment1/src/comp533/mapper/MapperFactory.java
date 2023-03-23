package comp533.mapper;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class MapperFactory extends AMapReduceTracer{
	private static MapperInterface<String, Integer> mapper;
    static {
        mapper = new Mapper<String, Integer>();
    }

    public static MapperInterface<String, Integer> getMapper() {
        return MapperFactory.mapper;
    }

    public static void setMapper(final MapperInterface<String, Integer> newMapper) {
    	MapperFactory.mapper = newMapper;
    	AMapReduceTracer.traceSingletonChange(MapperFactory.class, newMapper);
    }
}
