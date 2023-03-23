package nio;

import assignments.util.mainArgs.ClientArgsProcessor;

public class NioClientLaunch {
	public static void main(String[] args) {
		new NioClient(ClientArgsProcessor.getNIOServerPort(args)).processInput();
	}

}