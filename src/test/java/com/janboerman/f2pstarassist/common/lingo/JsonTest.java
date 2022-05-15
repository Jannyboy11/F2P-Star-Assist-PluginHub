package com.janboerman.f2pstarassist.common.lingo;

import com.janboerman.f2pstarassist.common.*;
import com.janboerman.f2pstarassist.common.util.CollectionConvert;
import static com.janboerman.f2pstarassist.common.web.StarJson.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonTest {

    private static final String[] GROUPS = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"};
    private static final StarLocation[] LOCATIONS = StarLocation.values();
    private static final StarTier[] TIERS = StarTier.values();
    private static final String[] USER_NAMES = {"Jannyboy11", "John Doe", "Alice", "Bob"};

    private final Random random = new Random();

    private Set<GroupKey> makeGroups() {
        int size = random.nextInt(3);

        Set<GroupKey> result = new HashSet<>();
        for (int i = 0; i < size; i++) {
            result.add(new GroupKey(GROUPS[i]));
        }
        return result;
    }

    private StarLocation makeStarLocation() {
        return LOCATIONS[random.nextInt(LOCATIONS.length)];
    }

    private int makeWorld() {
        int diff = 576 - 301;
        return random.nextInt(diff) + 301;
    }

    private StarTier makeTier() {
        return TIERS[random.nextInt(TIERS.length)];
    }

    private Instant makeDetectedAt() {
        return Instant.now().plus(random.nextInt(7200), ChronoUnit.SECONDS);
    }

    private User makeDetectedBy() {
        switch (random.nextInt(3)) {
            case 0:
                return User.unknown();
            case 1:
                return new RunescapeUser(USER_NAMES[random.nextInt(USER_NAMES.length)]);
            case 2:
                return new DiscordUser(USER_NAMES[random.nextInt(USER_NAMES.length)]);
        }
        throw new RuntimeException("Non-exhaustive switch!");
    }

    private StarKey makeStarKey() {
        return new StarKey(makeStarLocation(), makeWorld());
    }

    private StarUpdate makeStarUpdate() {
        return new StarUpdate(makeStarKey(), makeTier());
    }

    private StarRequest makeStarRequest() {
        return new StarRequest(Stream.generate(this::makeCrashedStar).limit(random.nextInt(10)).collect(Collectors.toSet()));
    }

    private CrashedStar makeCrashedStar() {
        return new CrashedStar(makeTier(), makeStarLocation(), makeWorld(), makeDetectedAt(), makeDetectedBy());
    }

    private StarPacket makeStarPacket() {
        Payload payload;
        switch (random.nextInt(4)) {
            case 0: payload = makeCrashedStar(); break;
            case 1: payload = makeStarKey(); break;
            case 2: payload = makeStarUpdate(); break;
            case 3: payload = makeStarRequest(); break;
            default: payload = null;
        }
        return new StarPacket(makeGroups(), payload);
    }

    private StarList makeStarList() {
        final Map<Set<CrashedStar>, Set<GroupKey>> fresh = new HashMap<>();
        final Set<StarUpdate> updates = new HashSet<>();
        final Set<StarKey> deleted = new HashSet<>();

        final int maxFresh = random.nextInt(4);
        final int maxUpdates = random.nextInt(4);
        final int maxDeleted = random.nextInt(4);

        for (int i = 0; i < maxFresh; i++) {
            Set<CrashedStar> starSet = Stream.generate(this::makeCrashedStar).limit(random.nextInt(4)).collect(Collectors.toSet());
            Set<GroupKey> groupSet = Arrays.stream(GROUPS).map(GroupKey::new).limit(random.nextInt(GROUPS.length)).collect(Collectors.toSet());
            fresh.put(starSet, groupSet);
        }
        for (int i = 0; i < maxUpdates; i++) {
            Set<StarUpdate> updateSet = Stream.generate(this::makeStarUpdate).limit(random.nextInt(4)).collect(Collectors.toSet());
            updates.addAll(updateSet);
        }
        for (int i = 0; i < maxDeleted; i++) {
            Set<StarKey> deleteSet = Stream.generate(this::makeStarKey).limit(random.nextInt(4)).collect(Collectors.toSet());
            deleted.addAll(deleteSet);
        }

        return new StarList(fresh, updates, deleted);
    }

    @Test
    public void testStarList() {
        final StarList starList = makeStarList();

        assertEquals(starList, starList(starListJson(starList)));
    }

    @Test
    public void testStarPacket() {
        final StarPacket starPacket = makeStarPacket();

        assertEquals(starPacket, starPacket(starPacketJson(starPacket)));
    }

    @Test
    public void testCrashedStar() {
        final CrashedStar crashedStar = makeCrashedStar();

        assertEquals(crashedStar, crashedStar(crashedStarJson(crashedStar)));
    }

    @Test
    public void testCrashedStars() {
        final Set<CrashedStar> stars = CollectionConvert.toSet(Arrays.asList(makeCrashedStar(), makeCrashedStar(), makeCrashedStar()));
        final Set<CrashedStar> noStars = Collections.emptySet();

        assertEquals(stars, crashedStars(crashedStarsJson(stars)));
        assertEquals(noStars, crashedStars(crashedStarsJson(noStars)));
    }

    @Test
    public void testStarUpdate() {
        final StarUpdate starUpdate = makeStarUpdate();

        assertEquals(starUpdate, starUpdate(starUpdateJson(starUpdate)));;
    }

    @Test
    public void testStarKey() {
        final StarKey starKey = makeStarKey();

        assertEquals(starKey, starKey(starKeyJson(starKey)));
    }

    @Test
    public void testGroupKeys() {
        final Set<GroupKey> groups = makeGroups();

        assertEquals(groups, groupKeys(groupKeysJson(groups)));
    }

}
