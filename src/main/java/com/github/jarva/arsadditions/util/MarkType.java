package com.github.jarva.arsadditions.util;

public enum MarkType {
    ENTITY,
    LOCATION,
    EMPTY,
    BROKEN;

    public static MarkType valueOfDefaulted(String markType) {
        try {
            return MarkType.valueOf(markType);
        } catch (IllegalArgumentException e) {
            return MarkType.EMPTY;
        }
    }
}
