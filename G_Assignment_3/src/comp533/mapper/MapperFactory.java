package comp533.mapper;

public class MapperFactory {
    private static MapperInterface mapper;
    static {
        mapper = new Mapper();
    }

    public static MapperInterface getMapper() {
        return MapperFactory.mapper;
    }

    public static void setMapper(MapperInterface mapper) {
        MapperFactory.mapper = mapper;
    }
}
