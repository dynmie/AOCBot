package me.dynmie.aoc.yukino.utils;

import java.awt.*;

/**
 * @author dynmie
 */
public enum EmbedLevel {

    WARNING(new Color(240, 213, 0)),
    DEFAULT(Colors.DEFAULT),
    ERROR(new Color(208, 23, 23)),
    DANGER(new Color(210, 39, 28));

    private final Color color;

    EmbedLevel(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
