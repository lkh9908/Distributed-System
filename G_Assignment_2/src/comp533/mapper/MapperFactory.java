package comp533.mapper;

public class MapperFactory {
    private static Mapper mapper;
    static {
        mapper = new Mapper();
    }

    public static Mapper getMapper() {
        return MapperFactory.mapper;
    }

    public static void setMapper(Mapper mapper) {
        MapperFactory.mapper = mapper;
    }
}
