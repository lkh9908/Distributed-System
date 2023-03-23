package comp533.controller;

import comp533.model.ModelInterface;
import comp533.model.TokenCounterModel;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.util.Scanner;


public class TokenCounterController extends AMapReduceTracer {
	private ModelInterface testModel = null;
	private TokenCounterView testView = null;
	
    public void getUserInput(final ModelInterface model, final TokenCounterView view) {
        final Scanner inputHandler = new Scanner(System.in);
        this.traceThreadPrompt();
        final int numThreads = inputHandler.nextInt();
        inputHandler.nextLine();
//        model.setNumThreads(numThreads, view);
        this.testModel = model;
        this.testView = view;
        setNumThreads(numThreads);
        while (true) {
            this.traceNumbersPrompt();
            final String line = inputHandler.nextLine();
            if (line.equals(AMapReduceTracer.QUIT)) {
                model.interruptThreads();
                this.traceQuit();
                inputHandler.close();
                break;
            }
            model.setInputString(line, view);
        }
    }

    public void setNumThreads(final int numThreads) {
        this.testModel.setNumThreads(numThreads, testView);
    }
    
    @Override
    public String toString() {
        return AMapReduceTracer.CONTROLLER;
    }
}
