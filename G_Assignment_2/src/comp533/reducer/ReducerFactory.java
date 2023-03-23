package comp533.reducer;

public class ReducerFactory {
    private static ReducerInterface<String, Integer> reducer;
    static {
        reducer = new Reducer();
    }

    public static ReducerInterface<String, Integer> getReducer() {
        return reducer;
    }

    public static void setReducer(final ReducerInterface<String, Integer> newReducer) {
        ReducerFactory.reducer = newReducer;
    }
}
