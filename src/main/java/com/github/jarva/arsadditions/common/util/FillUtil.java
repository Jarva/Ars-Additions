package com.github.jarva.arsadditions.common.util;

public class FillUtil {
    public static float getFillLevel(int source) {
        return getFillLevel(source, getMaxSource());
    }

    public static float getFillLevel(int source, int max) {
        return getFillLevel((float) source, max);
    }

    public static float getFillLevel(float source) {
        return getFillLevel(source, getMaxSource());
    }

    public static float getFillLevel(float source, float max) {
        return source / (max / 10);
    }

    public static int getFillState(int source) {
        return getFillState(source, getMaxSource());
    }

    public static int getFillState(int source, int max) {
        float level = getFillLevel(source, max);

        if (level == 0) return 0;
        return (int) (level + 1);
    }

    public static int getMaxSource() {
        return 10000;
    }
}
