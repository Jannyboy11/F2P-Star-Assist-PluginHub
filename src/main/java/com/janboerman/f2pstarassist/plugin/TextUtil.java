package com.janboerman.f2pstarassist.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final Pattern CHAT_NAME_PATTERN = Pattern.compile("(<img=\\d+>)?(?<rsn>.*)");

    private TextUtil() {
    }

    /**
     * Strips the player's chat icon from the name.
     * Examples of 'chat icon's are pmod/jmod crowns, or iron man mode helmet.
     * @param chatName the player's name in chat
     * @return the player's name without the icon prefixed
     */
    public static String stripChatIcon(String chatName) {
        Matcher matcher = CHAT_NAME_PATTERN.matcher(chatName);
        if (matcher.find()) {
            return matcher.group("rsn");
        } else {
            return chatName;
        }
    }

}
