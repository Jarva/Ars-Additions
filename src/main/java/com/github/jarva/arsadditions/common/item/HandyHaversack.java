package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.util.LangUtil;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HandyHaversack extends Item {
    public HandyHaversack() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;

        if (level.getGameTime() % 10 == 0) return;

        HaversackData.fromItemStack(stack).ifPresent(data -> {
            CompoundTag tag = stack.getOrCreateTag();
            boolean curr = tag.contains("loaded") && tag.getBoolean("loaded");
            boolean loaded = level.isLoaded(data.pos);
            if (curr != loaded) {
                tag.putBoolean("loaded", loaded);
            }
        });
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.isSecondaryUseActive()) return InteractionResult.PASS;
        if (context.getLevel().isClientSide()) return InteractionResult.CONSUME;

        BlockPos pos = context.getClickedPos();
        BlockEntity be = context.getLevel().getBlockEntity(pos);
        if (be == null) return InteractionResult.PASS;

        IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
        if (handler != null) {
            ItemStack stack = context.getItemInHand();

            HaversackData data = new HaversackData(pos, context.getLevel().dimension());
            data.write(stack);

            if (context.getPlayer() != null) {
                context.getPlayer().displayClientMessage(Component.translatable("chat.ars_additions.warp_index.bound", LangUtil.container()), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack haversack, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player) && slot.container instanceof Inventory) {
            ItemStack other = slot.getItem();
            return transportItem(haversack, other, player, slot::set);
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack haversack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            return transportItem(haversack, other, player, access::set);
        }
        return false;
    }

    public boolean transportItem(ItemStack haversack, ItemStack other, Player player, Consumer<ItemStack> update) {
        if (player.isCreative()) return true;

        Optional<HaversackData> dataOpt = HaversackData.fromItemStack(haversack);
        if (dataOpt.isEmpty()) return true;

        HaversackData data = dataOpt.get();
        FilterableItemHandler handler = data.getItemHandler(player.level());
        if (handler == null) return true;

        InventoryManager manager = new InventoryManager(List.of(handler));
        ItemStack remainder = manager.insertStack(other);

        update.accept(remainder);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        HaversackData.fromItemStack(stack).ifPresentOrElse(data -> {
            tooltip.add(Component.translatable("tooltip.ars_additions.warp_index.bound", data.pos.getX(), data.pos.getY(), data.pos.getZ(), data.level.location().toString()));
        }, () -> {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        });

        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue())) {
            tooltip.add(Component.translatable("tooltip.ars_additions.handy_haversack.instructions"));
        }
    }

    public record HaversackData(BlockPos pos, ResourceKey<Level> level) {
        public static final String TAG_KEY = "haversack";

        public static final Codec<HaversackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("BindPos").forGetter(HaversackData::pos),
            Level.RESOURCE_KEY_CODEC.fieldOf("BindDim").forGetter(HaversackData::level)
        ).apply(instance, HaversackData::new));

        public static Optional<HaversackData> fromItemStack(ItemStack stack) {
            return CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().getCompound(TAG_KEY)).result();
        }

        public void write(ItemStack stack) {
            CODEC.encodeStart(NbtOps.INSTANCE, this).result().ifPresent(tag -> {
                stack.getOrCreateTag().put(TAG_KEY, tag);
            });
        }

        @Nullable
        public FilterableItemHandler getItemHandler(Level level) {
            if (!level.isLoaded(pos)) return null;

            BlockEntity be = level.getBlockEntity(pos);
            if (be == null) return null;

            return InvUtil.getFilteredHandler(be);
        }
    }
}
