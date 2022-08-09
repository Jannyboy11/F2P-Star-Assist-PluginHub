package com.janboerman.f2pstarassist.common;

public enum StarLocation {

    WILDERNESS_RUNITE_MINE("Wilderness runite mine"),
    WILDERNESS_CENTRE_MINE("Wilderness centre mine"),
    WILDERNESS_SOUTH_WEST_MINE("Wilderness south west mine"),
    WILDERNESS_SOUTH_MINE("Wilderness south mine"),

    DWARVEN_MINE("Dwarven Mine"),
    MINING_GUILD("Mining Guild"),
    CRAFTING_GUILD("Crafting Guild"),
    RIMMINGTON_MINE("Rimmington mine"),

    DRAYNOR_VILLAGE_BANK("Draynor Village bank"),
    LUMBRIDGE_SWAMP_SOUTH_WEST_MINE("Lumbridge Swamp west mine"),
    LUMBRIDGE_SWAMP_SOUTH_EAST_MINE("Lumbridge Swamp east mine"),

    VARROCK_SOUTH_WEST_MINE("Varrock south west mine"),
    VARROCK_SOUTH_EAST_MINE("Varrock south east mine"),
    VARROCK_AUBURY("Varrock east bank"),

    AL_KHARID_MINE("Al Kharid mine"),
    AL_KHARID_BANK("Al Kharid bank"),
    PVP_ARENA("PvP Arena"),

    CRANDOR_NORTH_MINE("Crandor north mine"),
    CRANDOR_SOUTH_MINE("Crandor south mine"),
    CORSAIR_COVE_BANK("Corsair Cove bank"),
    CORSAIR_COVE_RESOURCE_AREA("Corsair Cove resource area");

    private final String humanFriendlyName;

    private StarLocation(String name) {
        this.humanFriendlyName = name;
    }

    @Override
    public String toString() {
        return humanFriendlyName;
    }

    public boolean isInWilderness() {
        switch (this) {
            case WILDERNESS_RUNITE_MINE:
            case WILDERNESS_CENTRE_MINE:
            case WILDERNESS_SOUTH_WEST_MINE:
            case WILDERNESS_SOUTH_MINE:
                return true;
            default:
                return false;
        }
    }

}
