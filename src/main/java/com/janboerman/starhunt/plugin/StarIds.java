package com.janboerman.starhunt.plugin;

import com.janboerman.starhunt.common.StarTier;

import net.runelite.api.ObjectID;

public final class StarIds {

    static final int TIER_9 = ObjectID.CRASHED_STAR;        //41020
    static final int TIER_8 = ObjectID.CRASHED_STAR_41021;  //41021
    static final int TIER_7 = ObjectID.CRASHED_STAR_41223;  //41223
    static final int TIER_6 = ObjectID.CRASHED_STAR_41224;  //41224
    static final int TIER_5 = ObjectID.CRASHED_STAR_41225;  //41225
    static final int TIER_4 = ObjectID.CRASHED_STAR_41226;  //41226
    static final int TIER_3 = ObjectID.CRASHED_STAR_41227;  //41227
    static final int TIER_2 = ObjectID.CRASHED_STAR_41228;  //41228
    static final int TIER_1 = ObjectID.CRASHED_STAR_41229;  //41229

    //rubble
    static final int FILLER_RUBBLE = 29733; //used in north-west, north-east, south-east corners

    private StarIds() {
    }

    public static StarTier getTier(int gameObjectId) {
        switch (gameObjectId) {
            case StarIds.TIER_1: return StarTier.SIZE_1;
            case StarIds.TIER_2: return StarTier.SIZE_2;
            case StarIds.TIER_3: return StarTier.SIZE_3;
            case StarIds.TIER_4: return StarTier.SIZE_4;
            case StarIds.TIER_5: return StarTier.SIZE_5;
            case StarIds.TIER_6: return StarTier.SIZE_6;
            case StarIds.TIER_7: return StarTier.SIZE_7;
            case StarIds.TIER_8: return StarTier.SIZE_8;
            case StarIds.TIER_9: return StarTier.SIZE_9;
            default: return null;
        }
    }

}

