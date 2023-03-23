package comp533;

import comp533.model.ModelInterface;
import comp533.model.TokenCounterModel;
import comp533.view.TokenCounterView;
import comp533.controller.TokenCounterController;

public class MvcTokenCounter {
	public static void main(final String[] args) {
        final TokenCounterController controller = new TokenCounterController();
        final ModelInterface model = new TokenCounterModel();
        final TokenCounterView view = new TokenCounterView();
        controller.getUserInput(model, view);
    }
}
