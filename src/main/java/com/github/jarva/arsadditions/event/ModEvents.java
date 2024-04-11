package com.github.jarva.arsadditions.event;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.advancement.Triggers;
import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ServerModEvents {
        @SubscribeEvent
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeTabRegistry.BLOCKS.getKey()) {
                for (RegistryObject<Item> item : AddonItemRegistry.REGISTERED_ITEMS) {
                    event.accept(item);
                }

                for (RegistryObject<? extends Block> block : AddonBlockRegistry.REGISTERED_BLOCKS) {
                    event.accept(block);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ServerForgeEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void isPlayerInStructure(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                MinecraftServer server = event.getServer();
                if (server.getTickCount() % 20 != 0) return;
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    boolean isRuinedPortal = player.serverLevel().structureManager().getStructureWithPieceAt(player.blockPosition(), TagKey.create(Registries.STRUCTURE, ArsAdditions.prefix("ruined_portals"))).isValid();
                    if (isRuinedPortal) {
                        Triggers.FIND_RUINED_PORTAL.trigger(player);
                    }
                }
            }
        }
    }
}
