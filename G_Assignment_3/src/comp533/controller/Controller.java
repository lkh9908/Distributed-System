package comp533.controller;

import comp533.main.RemoteModel;
import comp533.view.ViewInterface;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Controller extends AMapReduceTracer implements ControllerInterface {
    private Scanner inputHandler;
    private RemoteModel counter;
    private ViewInterface view;

    public Controller(RemoteModel counter, ViewInterface view) {
        this.inputHandler = new Scanner(System.in);
        this.counter = counter;
        this.view = view;
    }

    public void getUserInput(RemoteModel counter) {
        this.traceThreadPrompt();
        int numThreads = this.inputHandler.nextInt();
        this.inputHandler.nextLine();
        try {
            counter.setNumThreads(numThreads, this.view);
            while (true) {
                this.traceNumbersPrompt();
                String line = inputHandler.nextLine();
                if (line.equals(AMapReduceTracer.QUIT)) {
                    counter.interruptThreads();
                    counter.callClientQuit();
                    this.traceQuit();
                    break;
                }
                counter.setInputString(line, this.view);
            }
        } catch (RemoteException ex) {
            ex.getStackTrace();
        }
    }


    @Override
    public String toString() {
        return ControllerInterface.LABEL;
    }
}
