package comp533.view;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;

public class TokenCounterView extends AMapReduceTracer {

	// print out new model updates
	public void propertyChange(final PropertyChangeEvent event) {
        this.tracePropertyChange(event);
    }

    public String toString() {
        return AMapReduceTracer.VIEW;
    }
}