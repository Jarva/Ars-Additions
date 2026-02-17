package com.github.jarva.arsadditions.common.memory.handlers;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Memory handler for Starbuncle entities
 */
public class StarbuncleMemoryHandler extends MemoryHandler {

    public StarbuncleMemoryHandler() {
        super(ArsAdditions.prefix("starbuncle"));
    }

    @Override
    public boolean canSaveFrom(Entity entity) {
        return entity instanceof Starbuncle;
    }

    @Override
    public boolean canLoadTo(Entity entity) {
        return entity instanceof Starbuncle;
    }

    @Override
    public CompoundTag save(Entity entity) {
        if (!(entity instanceof Starbuncle starby)) {
            return new CompoundTag();
        }

        // Use the entity's own NBT serialization
        CompoundTag data = new CompoundTag();
        starby.addAdditionalSaveData(data);

        // Remove cosmetics, behavior type, and behavior settings
        if (data.contains("starbuncleData")) {
            CompoundTag starbuncleData = data.getCompound("starbuncleData");
            starbuncleData.remove("cosmetic");
            starbuncleData.remove("behavior");
            starbuncleData.remove("behaviorTag");
            data.put("starbuncleData", starbuncleData);
        }

        return data;
    }

    @Override
    public void load(Entity entity, CompoundTag data, Player player) {
        if (!(entity instanceof Starbuncle starby)) {
            return;
        }

        // Use the entity's own NBT deserialization
        starby.readAdditionalSaveData(data);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("memory_handler.ars_additions.starbuncle");
    }

    @Override
    public void getTooltip(CompoundTag data, List<Component> tooltip) {
        StarbuncleCharmData charmData = NBTComponent.fromTag(
            StarbuncleCharmData.CODEC.codec(),
            data
        );

        if (charmData.getName().isPresent()) {
            tooltip.add(Component.literal("  ").append(charmData.getName().get()).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ItemsRegistry.STARBUNCLE_CHARM.get());
    }
}
