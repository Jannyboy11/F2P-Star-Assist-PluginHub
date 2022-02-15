package com.janboerman.starhunt.common.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.janboerman.starhunt.common.*;

import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StarJson {

    private StarJson() {
    }

    public static StarPacket starPacket(JsonObject starPacket) {
        Set<GroupKey> groups = readGroupKeys(starPacket);
        Payload payload = readPayload(starPacket);
        return new StarPacket(groups, payload);
    }

    public static JsonObject starPacketJson(StarPacket packet) {
        JsonObject result = new JsonObject();
        writeGroupKeys(result, packet.getGroups());
        writePayload(result, packet.getPayload());
        return result;
    }

    public static CrashedStar crashedStar(JsonObject crashedStar) {
        StarTier tier = readTier(crashedStar);
        StarLocation location = readLocation(crashedStar);
        int world = readWorld(crashedStar);
        Instant detectedAt = readDetectedAt(crashedStar);
        User discoveredBy = readDiscoveredBy(crashedStar);
        return new CrashedStar(tier, location, world, detectedAt, discoveredBy);
    }

    public static JsonObject crashedStarJson(CrashedStar crashedStar) {
        JsonObject result = new JsonObject();
        writeTier(result, crashedStar.getTier());
        writeLocation(result, crashedStar.getLocation());
        writeWorld(result, crashedStar.getWorld());
        writeDetectedAt(result, crashedStar.getDetectedAt());
        writeDiscoveredBy(result, crashedStar.getDiscoveredBy());
        return result;
    }

    public static Set<CrashedStar> crashedStars(JsonArray jsonArray) {
        Set<CrashedStar> result = new TreeSet<>();
        for (JsonElement element : jsonArray) {
            if (element instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) element;
                result.add(crashedStar(jsonObject));
            }
        }
        return result;
    }

    public static JsonArray crashedStarsJson(Set<CrashedStar> crashedStars) {
        JsonArray jsonArray = new JsonArray(crashedStars.size());
        for (CrashedStar star : crashedStars) {
            jsonArray.add(crashedStarJson(star));
        }
        return jsonArray;
    }

    public static StarUpdate starUpdate(JsonObject starUpdate) {
        StarTier tier = readTier(starUpdate);
        StarLocation location = readLocation(starUpdate);
        int world = readWorld(starUpdate);
        return new StarUpdate(tier, location, world);
    }

    public static JsonObject starUpdateJson(StarUpdate starUpdate) {
        JsonObject result = new JsonObject();
        writeTier(result, starUpdate.getTier());
        writeLocation(result, starUpdate.getLocation());
        writeWorld(result, starUpdate.getWorld());
        return result;
    }

    public static StarKey starKey(JsonObject starKey) {
        StarLocation starLocation = readLocation(starKey);
        int world = readWorld(starKey);
        return new StarKey(starLocation, world);
    }

    public static JsonObject starKeyJson(StarKey starKey) {
        JsonObject result = new JsonObject();
        writeLocation(result, starKey.getLocation());
        writeWorld(result, starKey.getWorld());
        return result;
    }

    public static Set<GroupKey> groupKeys(JsonArray groupKeys) {
        return StreamSupport.stream(groupKeys.spliterator(), false)
                .map(jsonElem -> new GroupKey(jsonElem.getAsString()))
                .collect(Collectors.toSet());
    }

    public static JsonArray groupKeysJson(Set<GroupKey> groupKeys) {
        return groupKeys.stream()
                .map(groupKey -> new JsonPrimitive(groupKey.toString()))
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    // ============================== helper methods ============================== \\

    private static StarTier readTier(JsonObject crashedStar) {
        return StarTier.bySize(crashedStar.get("tier").getAsInt());
    }

    private static void writeTier(JsonObject crashedStar, StarTier tier) {
        crashedStar.add("tier", new JsonPrimitive(tier.getSize()));
    }

    private static StarLocation readLocation(JsonObject crashedStar) {
        return StarLocation.valueOf(crashedStar.get("location").getAsString());
    }

    private static void writeLocation(JsonObject crashedStar, StarLocation starLocation) {
        crashedStar.add("location", new JsonPrimitive(starLocation.name()));
    }

    private static int readWorld(JsonObject crashedStar) {
        return crashedStar.get("world").getAsInt();
    }

    private static void writeWorld(JsonObject crashedStar, int world) {
        crashedStar.add("world", new JsonPrimitive(world));
    }

    private static Instant readDetectedAt(JsonObject crashedStar) {
        return Instant.ofEpochMilli(crashedStar.get("detected at").getAsLong());
    }

    private static void writeDetectedAt(JsonObject crashedStar, Instant detectedAt) {
        crashedStar.add("detected at", new JsonPrimitive(detectedAt.toEpochMilli()));
    }

    private static User readDiscoveredBy(JsonObject crashedStar) {
        JsonElement jsonElement = crashedStar.get("discovered by");
        if (jsonElement == null) return User.unknown();

        JsonObject user = jsonElement.getAsJsonObject();
        String type = user.get("type").getAsString();
        switch (type) {
            case "RuneScape": return new RunescapeUser(user.get("name").getAsString());
            case "Discord": return new DiscordUser(user.get("name").getAsString());
            default: return User.unknown();
        }
    }

    private static void writeDiscoveredBy(JsonObject crashedStar, User discoveredBy) {
        if (discoveredBy instanceof RunescapeUser) {
            RunescapeUser user = (RunescapeUser) discoveredBy;
            JsonObject jsonUser = new JsonObject();
            jsonUser.add("type", new JsonPrimitive("RuneScape"));
            jsonUser.add("name", new JsonPrimitive(user.getName()));
            crashedStar.add("discovered by", jsonUser);
        } else if (discoveredBy instanceof DiscordUser) {
            DiscordUser user = (DiscordUser) discoveredBy;
            JsonObject jsonUser = new JsonObject();
            jsonUser.add("type", new JsonPrimitive("Discord"));
            jsonUser.add("name", new JsonPrimitive(user.getName()));
            crashedStar.add("discovered by", jsonUser);
        }
    }

    private static Set<GroupKey> readGroupKeys(JsonObject starPacket) {
        JsonArray jsonArray = starPacket.getAsJsonArray("groups");
        return groupKeys(jsonArray);
    }

    private static void writeGroupKeys(JsonObject starPacket, Set<GroupKey> groupKeys) {
        starPacket.add("groups", groupKeysJson(groupKeys));
    }

    private static Payload readPayload(JsonObject starPacket) {
        JsonElement payload = starPacket.get("payload");
        String type = starPacket.get("type").getAsString();
        switch (type) {
            case "star key":
                JsonObject starKeyPayload = (JsonObject) payload;
                return starKey(starKeyPayload);
            case "crashed star":
                JsonObject crashedStarPayload = (JsonObject) payload;
                return crashedStar(crashedStarPayload);
            case "star update":
                JsonObject starUpdatePayload = (JsonObject) payload;
                return starUpdate(starUpdatePayload);
            default:
                return null;
        }
    }

    private static void writePayload(JsonObject starPacket, Payload payload) {
        if (payload instanceof StarKey) {
            StarKey starKey = (StarKey) payload;
            starPacket.add("type", new JsonPrimitive("star key"));
            starPacket.add("payload", starKeyJson(starKey));
        } else if (payload instanceof CrashedStar) {
            CrashedStar crashedStar = (CrashedStar) payload;
            starPacket.add("type", new JsonPrimitive("crashed star"));
            starPacket.add("payload", crashedStarJson(crashedStar));
        } else if (payload instanceof StarUpdate) {
            StarUpdate starUpdate = (StarUpdate) payload;
            starPacket.add("type", new JsonPrimitive("star update"));
            starPacket.add("payload", starUpdateJson(starUpdate));
        }
    }

}
