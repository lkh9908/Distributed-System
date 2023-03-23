package comp533.controller;

import comp533.main.RemoteModel;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public interface ControllerInterface {
    String LABEL = AMapReduceTracer.CONTROLLER;
    void getUserInput(RemoteModel counter);
}
