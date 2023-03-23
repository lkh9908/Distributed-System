package comp533;

import comp533.view.View;
import comp533.controller.Controller;
import comp533.main.RemoteModel;
import comp533.main.SummerModel;

public class SumMain {
	public static void main(final String[] args) {
        final Controller controller = new Controller(null, null);
        final RemoteModel model = new SummerModel();
        final View view = new View();
        controller.getUserInput(model);
    }
}
