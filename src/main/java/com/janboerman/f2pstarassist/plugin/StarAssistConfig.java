package com.janboerman.f2pstarassist.plugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("F2P Star Assist")
public interface StarAssistConfig extends Config {

	// ======================================== Groups ========================================	\\
	//																							\\

	@ConfigSection(
			name = "Groups Settings",
			description = "Settings for groups",
			position = 0,
			closedByDefault = true
	)
	public static final String GROUP_SETTINGS_SECTION = "Groups Settings";

	//																							\\
	//																							\\

	@ConfigItem(
			position = 0,
			keyName = "groups",
			name = "Groups",
			description = "Group names and group keys. Defined as a JSON Object where keys are group names, and values are group codes",
			section = GROUP_SETTINGS_SECTION
	)
	default String groups() {
		return "{\r\n" +
				"    \"My Clan\" : \"<clan_code_here>\",\r\n" +
				"    \"My fc\" : \"<fc_code_here>\"\r\n" +
				"}";
	}

	//																							\\
	// ======================================================================================== \\


	// ========================================= HTTP =========================================	\\
	//																							\\

	@ConfigSection(
			name = "Webserver Settings",
			description = "Settings for sending and receiving data from the webserver",
			position = 1,
			closedByDefault = true
	)
	public static final String HTTP_SETTINGS_SECTION = "HTTP Settings";

	//																							\\
	//																							\\

	@ConfigItem(
			position = 0,
			keyName = "http enabled",
			name = "Enable webserver communication",
			description = "Whether a connection with the webserver should be established",
			section = HTTP_SETTINGS_SECTION
	)
	default boolean httpConnectionEnabled() {
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "address",
			name = "Webserver URL",
			description = "The address of the webserver with which star locations are shared",
			section = HTTP_SETTINGS_SECTION
	)
	default String httpUrl() {
		return "http://localhost:8080";
	}

	//																							\\
	// ======================================================================================== \\


	// ======================================== Sharing ======================================= \\
	//  																						\\

	@ConfigSection(
			name = "Sharing Settings",
			description = "Settings for sharing with groups (only works when webserver communication is enabled)",
			position = 2,
			closedByDefault = false
	)
	public static final String SHARING_SETTINGS_SECTION = "Sharing Settings";

	//																							\\
	//																							\\

	@ConfigItem(
			position = 0,
			keyName = "share pvp-world stars",
			name = "Share PVP-world stars",
			description = "Whether to send stars in PVP-worlds",
			section = SHARING_SETTINGS_SECTION
	)
	default boolean sharePvpWorldStars() {
		return false;
	}

	@ConfigItem(
			position = 1,
			keyName = "share wilderness stars",
			name = "Share Wilderness stars",
			description = "Whether to send stars in the Wilderness",
			section = SHARING_SETTINGS_SECTION
	)
	default boolean shareWildernessStars() {
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "share found stars",
			name = "Share found-by-me stars",
			description = "Whether to share stars that you encounter in the world",
			section = SHARING_SETTINGS_SECTION
	)
	default boolean shareFoundStars() {
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "share found stars with groups",
			name = "Share stars I find with groups:",
			description = "With which group should stars that you find yourself be shared? (semicolon-separated list)",
			section = SHARING_SETTINGS_SECTION
	)
	default String getGroupsToShareFoundStarsWith() {
		return "My Clan;my fc";
	}

	@ConfigItem(
			position = 4,
			keyName = "share private chat calls",
			name = "Share private chat calls with groups:",
			description = "Share private chat calls with your group",
			section = SHARING_SETTINGS_SECTION
	)
	default String shareCallsReceivedByPrivateChat() {
		return "";
	}

	@ConfigItem(
			position = 5,
			keyName = "share friends chat calls",
			name = "Share friends chat calls with groups:",
			description = "Share friends chat calls with your group",
			section = SHARING_SETTINGS_SECTION
	)
	default String shareCallsReceivedByFriendsChat() {
		return "My fc";
	}

	@ConfigItem(
			position = 6,
			keyName = "share clan chat calls",
			name = "Share clan chat calls with groups:",
			description = "Share clan chat calls with your group",
			section = SHARING_SETTINGS_SECTION
	)
	default String shareCallsReceivedByClanChat() {
		return "My Clan";
	}

	@ConfigItem(
			position = 7,
			keyName = "share public chat calls",
			name = "Share public chat calls with groups:",
			description = "Share public chat calls with your group",
			section = SHARING_SETTINGS_SECTION
	)
	default String shareCallsReceivedByPublicChat() {
		return "";
	}

	//																							\\
	// ======================================================================================== \\


	// ===================================== Chat Analysis ==================================== \\
	//																							\\

	@ConfigSection(
			name = "Chat Analysis Settings",
			description = "Settings for whether to interpret star calls from chat messages",
			position = 3,
			closedByDefault = false
	)
	public static final String CHAT_SETTINGS_SECTION = "Chat Settings";

	//																							\\
	//																							\\

	@ConfigItem(
			position = 1,
			keyName = "clan chat",
			name = "Interpret clan chat star calls",
			description = "Check whether clan chat messages contain star calls",
			section = CHAT_SETTINGS_SECTION
	)
	default boolean interpretClanChat() {
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "friends chat",
			name = "Interpret friends chat star calls",
			description = "Check whether friends chat messages contain star calls",
			section = CHAT_SETTINGS_SECTION
	)
	default boolean interpretFriendsChat() {
		return true;
	}

	@ConfigItem(
			position = 3,
			keyName = "private chat",
			name = "Interpret private chat star calls",
			description = "Check whether private chat messages contain star calls",
			section = CHAT_SETTINGS_SECTION
	)
	default boolean interpretPrivateChat() {
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "public chat",
			name = "Interpret public chat star calls",
			description = "Check whether public chat messages contain star calls",
			section = CHAT_SETTINGS_SECTION
	)
	default boolean interpretPublicChat() {
		return true;
	}

	//																							\\
	// ======================================================================================== \\


	// ====================================== Hint Arrow ====================================== \\
	//																							\\

	@ConfigSection(
			name = "Miscellaneous Settings",
			description = "Settings that don't belong in any other category",
			position = 4,
			closedByDefault = false
	)
	public static final String MISCELLANEOUS = "Miscellaneous Settings";

	//																							\\
	//																							\\

	@ConfigItem(
			position = 13,
			keyName = "hint enabled",
			name = "Enable arrow hints",
			description = "Whether to display an arrow that hints to the target location",
			section = MISCELLANEOUS
	)
	default boolean hintArrowEnabled() {
		return false;
	}

	@ConfigItem(
			position = 14,
			keyName = "double hopping locations",
			name = "Mark double hopping location tiles",
			description = "Enable tile markers for double hopping locations.<br>" +
					"F2P Double Hopping spots:<br>" +
					"- Between Crafting Guild and Rimmington mine<br>" +
					"- Between Desert mine and PvP Arena<br>" +
					"- Between Aubury and Varrock east mine (1 tile diff)<br>" +
					"- Between Lumbridge Swamp south east and Al Kharid bank (3 tile diff)",
			section = MISCELLANEOUS
	)
	default boolean markDoubleHoppingTiles() {
		return false;
	}

	//																							\\
	// ========================================================================================	\\

}
