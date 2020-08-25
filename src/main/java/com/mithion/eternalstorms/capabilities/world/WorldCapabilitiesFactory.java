package com.mithion.eternalstorms.capabilities.world;

import java.util.concurrent.Callable;

public final class WorldCapabilitiesFactory implements Callable<WorldCapabilities> {

	@Override
	public WorldCapabilities call() throws Exception {
		return new WorldCapabilities();
	}

}
