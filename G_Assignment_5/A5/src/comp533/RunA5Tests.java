package comp533;

import gradingTools.comp533s20.assignment5.Assignment5Suite;
import util.trace.Tracer;

public class RunA5Tests {
	public static void main(String[] args) {
		Tracer.showInfo(true);
		Assignment5Suite.main(args);
		int timeOut = 60;
		Assignment5Suite.setProcessTimeOut(timeOut);
	}
}
