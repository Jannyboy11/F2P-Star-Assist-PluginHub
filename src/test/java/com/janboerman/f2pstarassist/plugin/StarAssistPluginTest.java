package com.janboerman.f2pstarassist.plugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StarAssistPluginTest {

	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(StarAssistPlugin.class);
		RuneLite.main(args);
	}

}