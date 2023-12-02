package me.dynmie.aoc.yukino.commands;

public enum CommandCategory {

    INFO("Info", ":notepad_spiral:"),
    AOC("AOC", ":radio:");

    private final String niceName;
    private final String emoji;

    CommandCategory(String niceName, String emoji) {
        this.niceName = niceName;
        this.emoji = emoji;
    }

    public String getNiceName() {
        return niceName;
    }

    public String getEmoji() {
        return emoji;
    }
}
