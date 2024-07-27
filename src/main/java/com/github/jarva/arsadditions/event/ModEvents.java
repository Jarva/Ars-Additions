package com.github.jarva.arsadditions.event;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.advancement.Triggers;
import com.github.jarva.arsadditions.common.commands.SetLootTableCommand;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.github.jarva.arsadditions.setup.config.CommonConfig;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

    @Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerForgeEvents {
        @SubscribeEvent
        public static void started(ServerStartedEvent _event) {
            for (int i = 0; i < DungeonLootTables.UNCOMMON_LOOT.size(); i++) {
                Supplier<ItemStack> supplier = DungeonLootTables.UNCOMMON_LOOT.get(i);
                ItemStack stack = supplier.get();
                if (!(stack.getItem() instanceof RitualTablet)) continue;

                DungeonLootTables.UNCOMMON_LOOT.set(i, () -> {
                    List<RitualTablet> tablets = new ArrayList<>(RitualRegistry.getRitualItemMap().values()).stream().filter(tablet -> {
                        if (tablet.ritual instanceof RitualChunkLoading) {
                            return (boolean) CommonConfig.COMMON.config.get("ritual_enabled").get();
                        }
                        return true;
                    }).toList();
                    if (tablets.isEmpty()) return ItemStack.EMPTY;
                    return new ItemStack(tablets.get(DungeonLootTables.r.nextInt(tablets.size())));
                });
            }
        }

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

        @SubscribeEvent
        public static void commandRegister(RegisterCommandsEvent event) {
            SetLootTableCommand.register(event.getDispatcher(), event.getBuildContext());
        }
    }
}
