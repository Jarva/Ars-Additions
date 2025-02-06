package com.github.jarva.arsadditions.common.util;

import java.util.HashMap;

public class CooldownManager<T> {
    private final HashMap<T, Long> cooldowns = new HashMap<>();

    public boolean shouldRun(T key, long current, int cooldown) {
        long timer = cooldowns.computeIfAbsent(key, (k) -> current);
        if (timer > current) {
            return false;
        }
        cooldowns.put(key, current + cooldown);
        return true;
    }
}
