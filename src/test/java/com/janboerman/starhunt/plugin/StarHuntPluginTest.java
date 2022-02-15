package com.janboerman.starhunt.plugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StarHuntPluginTest {

	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(StarHuntPlugin.class);
		RuneLite.main(args);
	}

}