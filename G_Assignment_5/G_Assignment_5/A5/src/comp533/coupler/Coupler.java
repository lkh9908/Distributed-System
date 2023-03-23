package comp533.coupler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import stringProcessors.HalloweenCommandProcessor;
import util.trace.trickOrTreat.LocalCommandObserved;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.CLIENT_OUT_COUPLER, DistributedTags.RMI, DistributedTags.GIPC})

public class Coupler extends AnAbstractSimulationParametersBean implements PropertyChangeListener, Serializable {
	HalloweenCommandProcessor observingSimulation;

	public Coupler (final HalloweenCommandProcessor newSimulation) {
		observingSimulation = newSimulation;
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent newEvent) {
		if (!newEvent.getPropertyName().equals("InputString")) {
			return;
		}
		
		String newCommand = (String) newEvent.getNewValue();
		getIPCMechanism();
		LocalCommandObserved.newCase(this, newCommand);
		observingSimulation.processCommand(newCommand);
	}

	

}
