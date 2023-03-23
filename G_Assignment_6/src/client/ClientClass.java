package client;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({ DistributedTags.CLIENT, DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO })
public class ClientClass {

//  public void experimentInput() {
//      long start = System.currentTimeMillis();
//      PerformanceExperimentStarted.newCase(this, start, commands);
//      boolean oldTrace = isTrace();
//      this.trace(false);
//      for (int i = 0; i < commands; i++) {
//          this.simulationCommand(tryCommand);
//      }
//      this.trace(oldTrace);
//      long end = System.currentTimeMillis();
//      PerformanceExperimentEnded.newCase(this, start, end, end - start, commands);
//      
//      
//      System.out.println("Printing out the time:");
//      System.out.println(end - start);
//  }
	public static void main(final String[] args) {
		try {
			final ClientInterfaceRmi client = new ClientRmiGipcNio();
			// started!!!
			client.start(args);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
