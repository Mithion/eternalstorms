package com.mithion.eternalstorms.capabilities.player;

import java.util.concurrent.Callable;

public class PlayerCapabilitiesFactory implements Callable<PlayerCapabilities>{

	@Override
	public PlayerCapabilities call() throws Exception {
		return new PlayerCapabilities();
	}

}
