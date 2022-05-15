package com.janboerman.f2pstarassist.plugin;

import com.janboerman.f2pstarassist.common.StarLocation;
import static com.janboerman.f2pstarassist.plugin.StarPoints.toLocation;
import static com.janboerman.f2pstarassist.plugin.StarPoints.fromLocation;

import net.runelite.api.coords.WorldPoint;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class StarPointsTest {

    @Test
    public void testStarLocationRoundTrip() {
        for (StarLocation starLocation : StarLocation.values()) {
            assertEquals(starLocation, toLocation(fromLocation(starLocation)));
        }
    }

    @Test
    public void testWorldPointRoundTrip() throws IllegalAccessException {
        for (Field field : StarPoints.class.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC && field.getType() == WorldPoint.class) {
                WorldPoint worldPoint = (WorldPoint) field.get(null);
                assertEquals(worldPoint, fromLocation(toLocation(worldPoint)));
            }
        }
    }

}
