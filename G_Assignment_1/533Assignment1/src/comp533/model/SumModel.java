package comp533.model;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import comp533.keyValue.KeyValue;
//import comp533.mapper.Mapper;
import comp533.mapper.MapperInterface;
import comp533.mapper.SumMapper;
import comp533.reducer.Reducer;
import comp533.reducer.ReducerInterface;
import comp533.view.TokenCounterView;

public class SumModel extends AMapReduceTracer implements ModelInterface {
	private String inputString;
    private Map<String, Integer> result;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    public SumModel() {
    	this.inputString = null;
    	this.result = null;
    }

	public static MapperInterface<String, Integer> getSumMapper() {
    	
//    	MapperInterface<String, Integer> mapper = new SumMapper<String, Integer>();
        
//    	AMapReduceTracer.traceMapperChange(SumModel.class, mapper);
    	return new SumMapper<String, Integer>();
    }
    
    public void setInputString(final String newInputString, final TokenCounterView view) {
        final String oldInputString = this.inputString;
        this.inputString = newInputString;
        
        //create and send event to view
        final PropertyChangeEvent SetInputStringEvent = new PropertyChangeEvent(this, "InputString",
                oldInputString, newInputString);
        view.propertyChange(SetInputStringEvent);
        this.propertyChangeSupport.firePropertyChange(SetInputStringEvent);
        this.computeResultMapReduce(newInputString, view);
    }
    

    
    public void computeResultMapReduce(final String thisInputString, final TokenCounterView view) {
    	final Map<String, Integer> oldResult = this.result;
    	
        final String[] tokens = thisInputString.split(" ");
        final List<String> tokensList = Arrays.asList(tokens);
        final MapperInterface<String, Integer> mappedKeyValue = new SumMapper<String, Integer>();
        final List<KeyValue<String, Integer>> resultMap = mappedKeyValue.map(tokensList);
        final ReducerInterface<String, Integer> reducer = new Reducer();
        this.result = reducer.reduce(resultMap);
        final PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        view.propertyChange(updateResultEvent);
        this.propertyChangeSupport.firePropertyChange(updateResultEvent);
    }
    
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
