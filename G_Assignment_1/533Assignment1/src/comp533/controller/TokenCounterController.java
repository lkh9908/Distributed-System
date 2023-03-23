package comp533.controller;

import comp533.model.ModelInterface;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.util.Scanner;

public class TokenCounterController extends AMapReduceTracer {
    public void getUserInput(final ModelInterface model, final TokenCounterView view) {
		final Scanner scan = new Scanner(System.in);
        
        while (true) {
            this.traceNumbersPrompt();
            final String line = scan.nextLine();
            if (line.equals(AMapReduceTracer.QUIT)) {
            	scan.close();
                this.traceQuit();
                break;
            }
            model.setInputString(line, view);
        }
    }

    @Override
    public String toString() {
        return AMapReduceTracer.CONTROLLER;
    }
}