package com.janboerman.f2pstarassist.plugin;

import com.google.gson.*;
import com.google.inject.Provides;
import com.janboerman.f2pstarassist.common.*;

import com.janboerman.f2pstarassist.common.lingo.StarLingo;
import static com.janboerman.f2pstarassist.common.util.CollectionConvert.toSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import okhttp3.Call;
import okio.Buffer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

@Slf4j
@PluginDescriptor(name = "F2P Star Assist")
public class StarAssistPlugin extends Plugin {

	//populated at construction
	private final StarCache starCache;
	private final WeakHashMap<StarKey, Set<GroupKey>> owningGroups = new WeakHashMap<>();
	private final Map<String, GroupKey> groups = new HashMap<>();

	//populated right after construction
	@Inject private Client client;
	@Inject private ClientToolbar clientToolbar;
	@Inject private StarAssistConfig config;
	@Inject private WorldService worldService;
	@Inject private ClientThread clientThread;

	//populated on start-up
	private StarClient starClient;
	private ScheduledExecutorService fetcherTimer;
	private StarAssistPanel panel;
	private NavigationButton navButton;

	public StarAssistPlugin() {
		this.starCache = new StarCache(removalNotification -> {
			if (removalNotification.wasEvicted()) { //'evicted' meaning: not explicitly removed by a 'StarCache#remove' call.
				//remove from sidebar.
				clientThread.invokeLater(this::updatePanel);
			}

			//if a hint arrow pointing to the removed star exists, then clear it.
			clientThread.invoke(() -> {
				CrashedStar removedStar = removalNotification.getValue();
				if (removedStar.getWorld() == client.getWorld()) {
					WorldPoint starPoint = StarPoints.fromLocation(removedStar.getLocation());
					if (client.hasHintArrow() && client.getHintArrowPoint().equals(starPoint)) {
						client.clearHintArrow();
					}
				}
			});
		});
	}

	@Provides
	StarAssistConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(StarAssistConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		this.starClient = injector.getInstance(StarClient.class);
		this.panel = new StarAssistPanel(this, config, clientThread);
		BufferedImage icon = ImageUtil.loadImageResource(StarAssistPlugin.class, "/icon.png");
		this.navButton = NavigationButton.builder()
				.tooltip("F2P Star Assist")
				.icon(icon)
				.priority(10)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		setGroups(loadGroups());

		fetcherTimer = Executors.newSingleThreadScheduledExecutor();
		fetcherTimer.scheduleAtFixedRate(() -> {
			clientThread.invoke(() -> fetchStarList(toSet(groups.values())));
		}, 0, 15, TimeUnit.MINUTES);

		log.info("F2P Star Assist started!");
	}

	@Override
	protected void shutDown() throws Exception {
		clientToolbar.removeNavigation(navButton);
		fetcherTimer.shutdownNow();
		fetcherTimer = null;

		log.info("F2P Star Assist stopped!");
	}

	public void fetchStarList(Set<GroupKey> sharingGroups) {
		if (config.httpConnectionEnabled()) {
			CompletableFuture<Set<CrashedStar>> starFuture = starClient.requestStars(sharingGroups);
			starFuture.whenCompleteAsync((stars, ex) -> {
				if (ex != null) {
					log.error("Error when receiving star data", ex);
				} else {
					log.debug("received stars from webserver: " + stars);
					clientThread.invoke(() -> {
						boolean foundNew = starCache.addAll(stars);
						if (foundNew)
							updatePanel();
					});
				}
			});
		}
	}

	public void hopAndHint(CrashedStar star) {
		assert !client.isClientThread();

		int starWorld = star.getWorld();
		int currentWorld = client.getWorld();

		if (currentWorld != starWorld) {
			net.runelite.http.api.worlds.WorldResult worldResult = worldService.getWorlds();
			if (worldResult != null) {
				clientThread.invoke(() -> {
					World world = rsWorld(worldResult.findWorld(starWorld));
					if (world != null) {
						client.hopToWorld(world);
					}

					if (config.hintArrowEnabled()) {
						WorldPoint starPoint = StarPoints.fromLocation(star.getLocation());
						client.setHintArrow(starPoint);
					}
				});
			}
		}
	}

	public void showHintArrow(boolean whetherTo) {
		assert client.isClientThread();

		int playerWorld = client.getWorld();
		for (CrashedStar star : starCache.getStars()) {
			if (star.getWorld() == playerWorld) {
				WorldPoint starPoint = StarPoints.fromLocation(star.getLocation());
				if (whetherTo) {
					client.setHintArrow(starPoint);
				} else if (client.hasHintArrow() && client.getHintArrowPoint().equals(starPoint)) {
					client.clearHintArrow();
				}
				break;
			}
		}
	}

	@Nullable
	private World rsWorld(net.runelite.http.api.worlds.World world) {
		if (world == null) return null;
		assert client.isClientThread();

		World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		return rsWorld;
	}

	private Set<GroupKey> getOwningGroups(StarKey starKey) {
		Set<GroupKey> groups = owningGroups.get(starKey);
		if (groups == null) groups = Collections.emptySet();
		return groups;
	}

	private Map<String, GroupKey> getGroups() {
		return groups;
	}

	private void setGroups(Map<String, GroupKey> groups) {
		this.groups.clear();
		this.groups.putAll(groups);
	}

	private Map<String, GroupKey> loadGroups() {
		return loadGroups(config.groups());
	}

	private Map<String, GroupKey> loadGroups(String groupsJson) {
		JsonParser jsonParser = new JsonParser();
		try {
			JsonElement jsonElement = jsonParser.parse(groupsJson);
			if (jsonElement instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) jsonElement;

				Map<String, GroupKey> result = new HashMap<>();
				for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					String groupName = entry.getKey();
					JsonElement groupKeyString = entry.getValue();
					GroupKey groupKey = new GroupKey(groupKeyString.getAsString());
					result.put(groupName, groupKey);
				}

				return result;
			} else {
				log.error("groups must be defined as a json object!");
				return Collections.emptyMap();
			}
		} catch (RuntimeException e) {
			log.error("Invalid groups JSON in config", e);
			return Collections.emptyMap();
		}
	}

	private Set<GroupKey> getSharingGroups(String groupName) {
		if (groupName == null || groupName.isEmpty()) return Collections.emptySet();
		return Arrays.stream(groupName.split(";"))
				.flatMap(name -> {
					GroupKey key = getGroups().get(name);
					if (key == null) return Stream.empty();
					else return Stream.of(key);
				})
				.collect(Collectors.toSet());
	}

	private Set<GroupKey> getSharingGroupsOwnFoundStars() {
		return getSharingGroups(config.getGroupsToShareFoundStarsWith());
	}

	private Set<GroupKey> getSharingGroupsFriendsChat() {
		return getSharingGroups(config.shareCallsReceivedByFriendsChat());
	}

	private Set<GroupKey> getSharingGroupsClanChat() {
		return getSharingGroups(config.shareCallsReceivedByClanChat());
	}

	private Set<GroupKey> getSharingGroupsPrivateChat() {
		return getSharingGroups(config.shareCallsReceivedByPrivateChat());
	}

	private Set<GroupKey> getSharingGroupsPublicChat() {
		return getSharingGroups(config.shareCallsReceivedByPublicChat());
	}

	//
	// ======= Star Cache Bookkeeping =======
	//

	private boolean shouldBroadcastStar(StarKey starKey) {
		if (!config.httpConnectionEnabled()) return false;

		net.runelite.http.api.worlds.World w = worldService.getWorlds().findWorld(starKey.getWorld());
		boolean isPvP = w.getTypes().contains(net.runelite.http.api.worlds.WorldType.PVP);
		boolean isWilderness = starKey.getLocation().isInWilderness();
		return (config.sharePvpWorldStars() || !isPvP) && (config.shareWildernessStars() || !isWilderness);
	}

	public void reportStarNew(CrashedStar star, Set<GroupKey> groupsToShareTheStarWith) {
		log.debug("reporting new star: " + star);

		final StarKey starKey = star.getKey();
		final boolean isNew = starCache.add(star) == null;
		if (isNew) {
			updatePanel();
			owningGroups.put(starKey, groupsToShareTheStarWith);
		}

		if (config.shareFoundStars() && shouldBroadcastStar(starKey)) {
			CompletableFuture<Optional<CrashedStar>> upToDateStar = starClient.sendStar(groupsToShareTheStarWith, star);
			upToDateStar.whenCompleteAsync((optionalStar, ex) -> {
				if (ex != null) {
					logServerError(ex);
				} else if (optionalStar.isPresent()) {
					CrashedStar theStar = optionalStar.get();
					clientThread.invoke(() -> {
						starCache.forceAdd(theStar);
						updatePanel();
					});
				}
			});
		}
	}

	public void reportStarUpdate(StarKey starKey, StarTier newTier, boolean broadcast) {
		log.debug("reporting star update: " + starKey + "->" + newTier);

		CrashedStar star = starCache.get(starKey);
		if (star.getTier() == newTier) return;

		star.setTier(newTier);
		updatePanel();

		if (broadcast && shouldBroadcastStar(starKey)) {
			Set<GroupKey> shareGroups = getOwningGroups(starKey);
			CompletableFuture<CrashedStar> upToDateStar = starClient.updateStar(shareGroups, starKey, newTier);
			upToDateStar.whenCompleteAsync((theStar, ex) -> {
				if (ex != null) {
					logServerError(ex);
				}

				else if (!theStar.equals(star)) {
					clientThread.invoke(() -> { starCache.forceAdd(theStar); updatePanel(); });
				}
			});
		}
	}

	public void reportStarGone(StarKey starKey, boolean broadcast) {
		log.debug("reporting star gone: " + starKey);
		//is there ever a situation in which we don't want to broadcast, regardless of the config?

		Set<GroupKey> broadcastGroups = removeStar(starKey);
		updatePanel();
		if (broadcast && shouldBroadcastStar(starKey) && broadcastGroups != null) {
			starClient.deleteStar(broadcastGroups, starKey);
		}
	}

	@Nullable
	Set<GroupKey> removeStar(StarKey starKey) {
		assert client.isClientThread();

		starCache.remove(starKey);
		return owningGroups.remove(starKey);
	}

	private void logServerError(Throwable ex) {
		log.warn("Unexpected result from web server", ex);
		if (ex instanceof ResponseException) {
			Call call = ((ResponseException) ex).getCall();
			log.debug("Request that caused it: " + call.request());
			Buffer buffer = new Buffer();
			try {
				call.request().body().writeTo(buffer);
				log.debug("Request body: " + buffer.readString(StandardCharsets.UTF_8));
			} catch (IOException e) {
				log.error("Error reading call request body", e);
			}
		}
	}

	private void updatePanel() {
		log.debug("Panel repaint!");
		assert client.isClientThread() : "updatePanel must be called from the client thread!";

		Set<CrashedStar> stars = new HashSet<>(starCache.getStars());
		SwingUtilities.invokeLater(() -> panel.setStars(stars));
	}


	//
	// ======= Helper methods =======
	//

	private static final int DETECTION_DISTANCE = 25;

	private boolean playerInStarRange(WorldPoint starLocation) {
		return playerInRange(starLocation, DETECTION_DISTANCE);
	}

	private boolean playerInRange(WorldPoint worldPoint, int distance) {
		return inManhattanRange(client.getLocalPlayer().getWorldLocation(), worldPoint, distance);
	}

	private static boolean inManhattanRange(WorldPoint playerLoc, WorldPoint targetLoc, int distance) {
		int playerX = playerLoc.getX();
		int playerY = playerLoc.getY();
		int starX = targetLoc.getX();
		int starY = targetLoc.getY();
		return playerLoc.getPlane() == targetLoc.getPlane()
				&& starX - distance <= playerX && playerX <= starX + distance
				&& starY - distance <= playerY && playerY <= starY + distance;
	}

	private static StarTier getStar(Tile tile) {
		for (GameObject gameObject : tile.getGameObjects()) {
			if (gameObject != null) {
				StarTier starTier = StarIds.getTier(gameObject.getId());
				if (starTier != null) return starTier;
			}
		}
		return null;
	}

	//
	// ======= Event Listeners =======
	//

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if ("F2P Star Assist".equals(event.getGroup())) {
			if ("groups".equals(event.getKey())) {
				try {
					setGroups(loadGroups(event.getNewValue()));
				} catch (RuntimeException e) {
					log.error("Invalid groups JSON in config", e);
				}
			}

			else if ("hint enabled".equals(event.getKey())) {
				showHintArrow(Boolean.parseBoolean(event.getNewValue()));
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		for (CrashedStar star : starCache.getStars()) {
			if (client.getWorld() == star.getWorld()) {
				WorldPoint starPoint = StarPoints.fromLocation(star.getLocation());
				if (starPoint != null && playerInStarRange(starPoint)) {
					LocalPoint localPoint = LocalPoint.fromWorld(client, starPoint);
					Tile tile = client.getScene().getTiles()[starPoint.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
					StarTier starTier = getStar(tile);
					if (starTier == null) {
						//a star that was in the cache is no longer there.
						reportStarGone(star.getKey(), true);
						if (starPoint.equals(client.getHintArrowPoint())) {
							client.clearHintArrow();
						}
					}

					else if (playerInRange(starPoint, 4) && starPoint.equals(client.getHintArrowPoint())) {
						//if the player got withing a range of 4, clear the arrow.
						client.clearHintArrow();
					}
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			showHintArrow(config.hintArrowEnabled());
		}
	}

	@Subscribe
	public void onWorldChanged(WorldChanged event) {
		showHintArrow(config.hintArrowEnabled());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		if (client.getGameState() != GameState.LOGGED_IN) return;	//player not in the world

		GameObject gameObject = event.getGameObject();
		StarTier starTier = StarIds.getTier(gameObject.getId());
		if (starTier == null) return;	//not a star

		WorldPoint worldPoint = gameObject.getWorldLocation();
		StarKey starKey = new StarKey(StarPoints.toLocation(worldPoint), client.getWorld());

		log.debug("A " + starTier + " star just despawned at location: " + worldPoint + ".");

		if (playerInStarRange(worldPoint)) {
			if (starTier == StarTier.SIZE_1) {
				//the star was mined out completely, or it poofed at t1.
				reportStarGone(starKey, true);
			} else {
				//it either degraded one tier, or disintegrated completely (poofed).
				//check whether a new star exists in the next game tick
				clientThread.invokeLater(() -> {
					if (client.getGameState() == GameState.LOGGED_IN && playerInStarRange(worldPoint)) {
						LocalPoint localStarPoint = LocalPoint.fromWorld(client, worldPoint);
						Tile tile = client.getScene().getTiles()[worldPoint.getPlane()][localStarPoint.getSceneX()][localStarPoint.getSceneY()];

						StarTier newTier = null;
						for (GameObject go : tile.getGameObjects()) {
							if (go != null) {
								StarTier tier = StarIds.getTier(go.getId());
								if (tier == starTier.oneLess()) {
									//a new star exists
									newTier = tier;
									break;
								}
							}
						}

						if (newTier == null) {
							//the star has poofed
							reportStarGone(starKey, true);
						} else {
							//the star has degraded
							reportStarUpdate(starKey, newTier, true);
						}
					}
				});
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		GameObject gameObject = event.getGameObject();
		StarTier starTier = StarIds.getTier(gameObject.getId());
		if (starTier == null) return;	//not a star

		WorldPoint worldPoint = gameObject.getWorldLocation();
		StarKey starKey = new StarKey(StarPoints.toLocation(worldPoint), client.getWorld());

		log.debug("A " + starTier + " star spawned at location: " + worldPoint + ".");

		CrashedStar knownStar = starCache.get(starKey);
		if (knownStar == null) {
			//we found a new star
			CrashedStar newStar = new CrashedStar(starKey, starTier, Instant.now(), new RunescapeUser(client.getLocalPlayer().getName()));
			reportStarNew(newStar, getSharingGroupsOwnFoundStars());
		}
	}

	// If stars degrade, they just de-spawn and spawn a new one at a lower tier. The GameObjectChanged event is never called.

	private boolean isWorld(int world) {
		net.runelite.http.api.worlds.WorldResult worldResult = worldService.getWorlds();
		if (worldResult == null) return false;
		return worldResult.findWorld(world) != null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		final String message = event.getMessage();

		StarLocation location;
		int world;
		StarTier tier;

		switch (event.getType()) {
			case FRIENDSCHAT:
				if (config.interpretFriendsChat()) {
					if ((tier = StarLingo.interpretTier(message)) != null
							&& (location = StarLingo.interpretLocation(message)) != null
							&& (world = StarLingo.interpretWorld(message)) != -1
							&& isWorld(world)) {
						CrashedStar star = new CrashedStar(tier, location, world, Instant.now(), new RunescapeUser(event.getName()));
						reportStarNew(star, getSharingGroupsFriendsChat());
					}
				}
				break;
			case CLAN_CHAT:
				if (config.interpretClanChat()) {
					if ((tier = StarLingo.interpretTier(message)) != null
							&& (location = StarLingo.interpretLocation(message)) != null
							&& (world = StarLingo.interpretWorld(message)) != -1
							&& isWorld(world)) {
						CrashedStar star = new CrashedStar(tier, location, world, Instant.now(), new RunescapeUser(event.getName()));
						reportStarNew(star, getSharingGroupsClanChat());
					}
				}
				break;
			case PRIVATECHAT:
			case MODPRIVATECHAT:
				if (config.interpretPrivateChat()) {
					if ((tier = StarLingo.interpretTier(message)) != null
							&& (location = StarLingo.interpretLocation(message)) != null
							&& (world = StarLingo.interpretWorld(message)) != -1
							&& isWorld(world)) {
						CrashedStar star = new CrashedStar(tier, location, world, Instant.now(), new RunescapeUser(event.getName()));
						reportStarNew(star, getSharingGroupsPrivateChat());
					}
				}
				break;
			case PUBLICCHAT:
			case MODCHAT:
				if (config.interpretPublicChat()) {
					if ((tier = StarLingo.interpretTier(message)) != null
							&& (location = StarLingo.interpretLocation(message)) != null
							&& (world = StarLingo.interpretWorld(message)) != -1
							&& isWorld(world)) {
						CrashedStar star = new CrashedStar(tier, location, world, Instant.now(), new RunescapeUser(event.getName()));
						reportStarNew(star, getSharingGroupsPublicChat());
					}
				}
				break;
		}
	}
}
