package comp533.view;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View extends AMapReduceTracer implements ViewInterface, PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.tracePropertyChange(event);
    }


    @Override
    public String toString() {
        return AMapReduceTracer.VIEW;
    }
}
