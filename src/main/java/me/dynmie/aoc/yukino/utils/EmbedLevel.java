package me.dynmie.aoc.yukino.utils;

import java.awt.*;

/**
 * @author dynmie
 */
public enum EmbedLevel {

    WARNING(new Color(238, 229, 40)),
    DEFAULT(Colors.DEFAULT),
    ERROR(new Color(236, 21, 21));

    private final Color color;

    EmbedLevel(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
