package comp533;

import comp533.controller.TokenCounterController;
import comp533.model.ModelInterface;
import comp533.model.SumModel;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class SumMain {
	public static void main(final String[] args) {
		AMapReduceTracer.traceSingletonChange(SumModel.class, SumModel.getSumMapper());
		final TokenCounterController controller = new TokenCounterController();
        final ModelInterface model = new SumModel();
        final TokenCounterView view = new TokenCounterView();
        controller.getUserInput(model, view);
	}
}
