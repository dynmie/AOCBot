package me.dynmie.aoc.yukino.locale;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.utils.Config;

import java.io.File;
import java.text.MessageFormat;

/**
 * @author dynmie
 */
public enum Lang {

    YOU_ALREADY_MEMBER("YOU_ALREADY_MEMBER", "You are already a member of AOC."),
    MEMBER_NOT_EXIST("MEMBER_NOT_EXIST", "That person isn't a part of AOC."),
    MEMBER_ALREADY_EXIST("MEMBER_ALREADY_EXIST", "That person is already a member of AOC."),
    MEMBER_SAVED("MEMBER_SAVED", "**{0}** has been saved."),
    MEMBER_REMOVED("MEMBER_REMOVED", "**{0}** is no longer a part of AOC."),
    MEMBER_REMOVE_CONFIRM("MEMBER_REMOVE_CONFIRM", "Are you sure you want to remove **{0}** from AOC?"),
    ERROR("ERROR", "An error occurred."),
    NO_PERMISSION("NO_PERMISSION", "You do not have permission to execute this command."),
    STRIKE_NOT_EXIST("STRIKE_NOT_EXIST", "That strike doesn't exist."),
    BUTTON_NOT_YOURS("BUTTON_NOT_YOURS", "That button isn't yours!"),
    STRIKE_CREATED("STRIKE_CREATED", ":warning: **{0}** has been striked with the reason of **{1}**. (#{2})"),
    STRIKES_RESET_CONFIRM("STRIKES_RESET_CONFIRM", "**You are about to delete all strikes stored in the database.**\nAre you sure you would like to continue?"),
    STRIKES_RESET("STRIKES_RESET", "All strikes has been reset."),
    MODAL_EXPIRED("MODAL_EXPIRED", "This modal has expired."),
    INTERACTION_CANCELLED("INTERACTION_CANCELLED", "The interaction has been cancelled.");

    private final String path;
    private final String defaultMessage;

    Lang(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String format(Object... args) {
        return MessageFormat.format(get(), args);
    }

    public String get() {
        if (langFile != null) {
            return langFile.getProperty(path, defaultMessage);
        }
        return defaultMessage;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    private static Config langFile = null;

    public static void init() {
        File file = new File(Yukino.getFolderPath() + File.separator + "lang.properties");
        if (file.exists()) {
            langFile = new Config(file, null);
        }
    }

}