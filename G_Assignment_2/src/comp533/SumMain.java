package comp533;

import comp533.model.ModelInterface;
import comp533.model.SumModel;
import comp533.view.TokenCounterView;
import comp533.controller.TokenCounterController;

public class SumMain {
	public static void main(final String[] args) {
        final TokenCounterController controller = new TokenCounterController();
        final ModelInterface model = new SumModel();
        final TokenCounterView view = new TokenCounterView();
        controller.getUserInput(model, view);
    }
}
