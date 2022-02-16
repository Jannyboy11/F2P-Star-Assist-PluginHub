package com.janboerman.f2pstarassist.common.lingo;

import com.janboerman.f2pstarassist.common.StarLocation;
import com.janboerman.f2pstarassist.common.StarTier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StarLingo {

    private static final Pattern TIER_PATTERN = Pattern.compile(".*?[t|T|s|S](ier|ize)?\\s*(?<tier>[1-9]).*");
    private static final Pattern WORLD_PATTERN = Pattern.compile(".*?([w|W](orld)?)?\\s*(?<world>\\d{3}).*");

    private StarLingo() {
    }

    /**
     * Try to get the world from a string of text. Note that the returned number may not be an existing world.
     * @param text the input
     * @return the world, or -1 if the text didn't contain a world
     */
    public static int interpretWorld(String text) {
        Matcher matcher = WORLD_PATTERN.matcher(text);
        if (matcher.matches()) {
            String world = matcher.group("world");
            return Integer.parseInt(world);
        }
        return -1;
    }

    /**
     * Try to get the tier of the star.
     * @param text the input
     * @return the star's tier, or null if the tier could not be recognised
     */
    public static StarTier interpretTier(String text) {
        Matcher matcher = TIER_PATTERN.matcher(text);
        if (matcher.matches()) {
            String tier = matcher.group("tier");
            switch (tier) {
                case "9": return StarTier.SIZE_9;
                case "8": return StarTier.SIZE_8;
                case "7": return StarTier.SIZE_7;
                case "6": return StarTier.SIZE_6;
                case "5": return StarTier.SIZE_5;
                case "4": return StarTier.SIZE_4;
                case "3": return StarTier.SIZE_3;
                case "2": return StarTier.SIZE_2;
                case "1": return StarTier.SIZE_1;
            }
        }
        return null;
    }

    /**
     * Try to get the location from a string of text.
     * @param text the input
     * @return the star's location, or null if the location couldn't be recognised
     */
    //TODO return a Set<StarLocation> instead? then it's possible to return multiple locations if it's ambiguous (e.g. just 'Crandor')
    public static StarLocation interpretLocation(String text) {
        //wildy
        if (containsAnyIgnoreCase(text, "wildy", "wilderness"))
            if (containsAnyIgnoreCase(text, "rune", "runite", "lava", "maze"))
                return StarLocation.WILDERNESS_RUNITE_MINE;
            else if (containsAnyIgnoreCase(text, "centre", "center", "bandit", "camp", "hobgoblins"))
                return StarLocation.WILDERNESS_CENTRE_MINE;
            else if (containsAnyIgnoreCase(text, "dark", "warrior", "fortress")
                    || containsAllIgnoreCase(text, "south", "west"))
                return StarLocation.WILDERNESS_SOUTH_WEST_MINE;
            else if ((containsIgnoreCase(text, "south") && !containsIgnoreCase(text, "west"))
                    || containsAnyIgnoreCase(text, "mage", "zamorak", "zammy"))
                return StarLocation.WILDERNESS_SOUTH_MINE;

        //dwarven mine, falador, rimmington, crafting guild
        if (containsAnyIgnoreCase(text, "dwarf", "dwarven"))
            return StarLocation.DWARVEN_MINE;
        if (containsAnyIgnoreCase(text, "falador", "fally", "mining guild"))
            return StarLocation.MINING_GUILD;
        if (containsIgnoreCase(text, "craft") && containsIgnoreCase(text, "guild"))
            return StarLocation.CRAFTING_GUILD;
        if (containsIgnoreCase(text, "rim"))
            return StarLocation.RIMMINGTON_MINE;

        //draynor, lumbridge
        if (containsIgnoreCase(text, "draynor"))
            return StarLocation.DRAYNOR_VILLAGE_BANK;
        if (containsAnyIgnoreCase(text, "lumbridge", "swamp", "lumby"))
            if (containsIgnoreCase(text, "west"))
                return StarLocation.LUMBRIDGE_SWAMP_SOUTH_WEST_MINE;
            else if (containsIgnoreCase(text, "east"))
                return StarLocation.LUMBRIDGE_SWAMP_SOUTH_EAST_MINE;
        if (containsIgnoreCase(text, "lsw"))
            return StarLocation.LUMBRIDGE_SWAMP_SOUTH_WEST_MINE;
        if (containsIgnoreCase(text, "lse"))
            return StarLocation.LUMBRIDGE_SWAMP_SOUTH_EAST_MINE;

        //varrock
        if (containsIgnoreCase(text, "varrock"))
            if (containsAnyIgnoreCase(text, "west", "sw"))
                return StarLocation.VARROCK_SOUTH_WEST_MINE;
            else if (containsAnyIgnoreCase(text, "east", "se"))
                return StarLocation.VARROCK_SOUTH_EAST_MINE;
            else
                return StarLocation.VARROCK_AUBURY;
        if (containsAnyIgnoreCase(text, "vsw", "champions guild", "vmw"))
            return StarLocation.VARROCK_SOUTH_WEST_MINE;
        if (containsAnyIgnoreCase(text, "vse", "vme"))
            return StarLocation.VARROCK_SOUTH_EAST_MINE;

        //al kharid, duel arena
        if (containsAllIgnoreCase(text, "al", "kharid") || containsAnyIgnoreCase(text, "alk", "ally"))
            if (containsIgnoreCase(text, "bank"))
                return StarLocation.AL_KHARID_BANK;
            else if (containsIgnoreCase(text, "mine"))
                return StarLocation.AL_KHARID_MINE;
        if (containsIgnoreCase(text, "duel"))
            return StarLocation.DUEL_ARENA;

        //crandor, corsair cove
        if (containsIgnoreCase(text, "crandor"))
            if (containsIgnoreCase(text, "north"))
                return StarLocation.CRANDOR_NORTH_MINE;
            else if (containsIgnoreCase(text, "south"))
                return StarLocation.CRANDOR_SOUTH_MINE;
        if (containsIgnoreCase(text, "corsair"))
            if (containsIgnoreCase(text, "bank"))
                return StarLocation.CORSAIR_COVE_BANK;
            else
                return StarLocation.CORSAIR_COVE_RESOURCE_AREA;

        //could not recognise location
        return null;
    }

    public static boolean containsAnyIgnoreCase(String string, String... lookups) {
        for (String lookup : lookups) if (containsIgnoreCase(string, lookup)) return true;
        return false;
    }

    public static boolean containsAllIgnoreCase(String string, String... lookups) {
        for (String lookup : lookups) if (!containsIgnoreCase(string, lookup)) return false;
        return true;
    }

    public static boolean containsIgnoreCase(String string, String lookup) {
        return string.toLowerCase().contains(lookup.toLowerCase());
    }

}
