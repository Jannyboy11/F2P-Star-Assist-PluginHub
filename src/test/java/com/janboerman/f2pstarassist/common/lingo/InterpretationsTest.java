package com.janboerman.f2pstarassist.common.lingo;

import com.janboerman.f2pstarassist.common.StarLocation;
import com.janboerman.f2pstarassist.common.StarTier;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InterpretationsTest {

    @Test
    public void testCorsairBank() {
        String text = "T2 corsair bank w308";

        assertEquals(StarLocation.CORSAIR_COVE_BANK, StarLingo.interpretLocation(text));
        assertEquals(StarTier.SIZE_2, StarLingo.interpretTier(text));
        assertEquals(308, StarLingo.interpretWorld(text));
    }

    @Test
    public void testLumbridgeSwampEast() {
        String text = "Yea w335 tier 2 lumby east";

        assertEquals(StarLocation.LUMBRIDGE_SWAMP_SOUTH_EAST_MINE, StarLingo.interpretLocation(text));
        assertEquals(StarTier.SIZE_2, StarLingo.interpretTier(text));
        assertEquals(335, StarLingo.interpretWorld(text));
    }

    @Test
    public void testVarrock() {
        String text = "T3 Varrock East W382";

        assertEquals(StarLocation.VARROCK_SOUTH_EAST_MINE, StarLingo.interpretLocation(text));
        assertEquals(StarTier.SIZE_3, StarLingo.interpretTier(text));
        assertEquals(382, StarLingo.interpretWorld(text));
    }

    @Test
    public void testAlKharidBank(){
        String text = "t1 w453 al kharid bank";

        assertEquals(StarLocation.AL_KHARID_BANK, StarLingo.interpretLocation(text));
        assertEquals(StarTier.SIZE_1, StarLingo.interpretTier(text));
        assertEquals(453, StarLingo.interpretWorld(text));
    }

    @Test
    public void testAubury(){
        String text = "316 aubury t7";

        assertEquals(StarLocation.VARROCK_AUBURY, StarLingo.interpretLocation(text));
        assertEquals(StarTier.SIZE_7, StarLingo.interpretTier(text));
        assertEquals(316, StarLingo.interpretWorld(text));
    }
}
