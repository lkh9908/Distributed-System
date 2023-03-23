package nio;

import assignments.util.mainArgs.ServerArgsProcessor;

public class NioServerLaunch {
	public static void main(String[] args) {
		new NioServer(ServerArgsProcessor.getNIOServerPort(args));
	}

}