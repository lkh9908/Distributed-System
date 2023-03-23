package comp533.model;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.MapperInterface;
import comp533.reducer.Reducer;
import comp533.reducer.ReducerInterface;
import comp533.view.TokenCounterView;

public class TokenCounterModel extends AMapReduceTracer  implements ModelInterface {
	private String inputString;
    private Map<String, Integer> result;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String blank = " ";
    
    public TokenCounterModel() {
    	this.inputString = null;
    	this.result = null;
    }

    public void setInputString(final String newInputString, final TokenCounterView view) {
        final String oldInputString = this.inputString;
        this.inputString = newInputString;
        
        //create and send event to view
        final PropertyChangeEvent SetInputStringEvent = new PropertyChangeEvent(this, "InputString",
                oldInputString, newInputString);
        view.propertyChange(SetInputStringEvent);
        this.propertyChangeSupport.firePropertyChange(SetInputStringEvent);
        this.computeResultMapReduce(newInputString);
        this.computeResult(view);
    }
    
    public void computeResult(final TokenCounterView view) {
    	final Map<String, Integer> oldResult = this.result;
    	final String[] tokens = this.inputString.split(blank);
        
        this.result = this.tokenMapGenerator(tokens);
        final PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        view.propertyChange(updateResultEvent);
        this.propertyChangeSupport.firePropertyChange(updateResultEvent);
    }
    
    public void computeResultMapReduce(final String thisInputString) {
        final String[] tokens = thisInputString.split(blank);
        final List<String> tokensList = Arrays.asList(tokens);
        final MapperInterface<String, Integer> mappedKeyValue = new Mapper<String, Integer>();
        final List<KeyValue<String, Integer>> resultMap = mappedKeyValue.map(tokensList);
//        Map<String, Integer> resultMap = this.tokenMapGenerator(tokens);
        final ReducerInterface<?, ?> reducer = new Reducer();
        reducer.reduce(resultMap);
    }
    
    // not reduce, brute force
    private Map<String, Integer> tokenMapGenerator(final String[] tokens) {
        final Map<String, Integer> map = new HashMap<>();
        for (String token: tokens) {
            if (!map.containsKey(token)) {
                map.put(token, 1);
            } else {
                final int value = map.get(token);
                map.put(token, value + 1);
            }
        }
        return map;
    }
    
//    public void computeResultMapReduce(String inputString) {
//        String[] tokens = inputString.split(" ");
//        TokenCountingMapper mappedKeyValue = new TokenCountingMapper(tokens);
//        mappedKeyValue.printMapper();
//        Map<String, Integer> resultMap = this.formMap(tokens);
//        TokenCountingReducer reducer = new TokenCountingReducer(mappedKeyValue, resultMap);
//        reducer.printReducer();
//    }
    
    public String getInputString() {
    	return this.inputString;
    }
	    
    public Map<String, Integer> getResult() {
        return this.result;
    }
    
    @Override
    public String toString() {
    	return AMapReduceTracer.MODEL;
    }
}
