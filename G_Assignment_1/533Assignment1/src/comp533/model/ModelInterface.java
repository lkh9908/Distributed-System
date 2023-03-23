package comp533.model;

import java.util.Map;

import comp533.view.TokenCounterView;

public interface ModelInterface {
	public void setInputString(String newInputString, TokenCounterView view);
	
	public String getInputString();
	    
    public Map<String, Integer> getResult();

}
