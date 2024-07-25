package com.github.jarva.arsadditions.server.util;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PlayerInvUtil {

    public static <T> T findItem(Player player, Predicate<ItemStack> is, T def, Function<ItemStack, T> map) {
        T result = findItem((LivingEntity) player, is, def, map);

        if (result == def) {
            Inventory inv = player.getInventory();
            int size = inv.getContainerSize();
            for(int i = 0;i<size;i++) {
                ItemStack s = inv.getItem(i);
                if(is.test(s)) {
                    return map.apply(s);
                }
            }
        }

        return result;
    }

    public static <T> T findItem(LivingEntity player, Predicate<ItemStack> is, T def, Function<ItemStack, T> map) {
        if(is.test(player.getMainHandItem())) return map.apply(player.getMainHandItem());
        if(is.test(player.getOffhandItem())) return map.apply(player.getOffhandItem());

        Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
        if (opt.isPresent()) {
            List<SlotResult> s = opt.get().findCurios(is);
            if (!s.isEmpty()) return map.apply(s.get(0).stack());
        }

        return def;
    }
}
