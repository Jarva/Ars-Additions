package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.server.util.PlayerInvUtil;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ArsAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandyHaversack extends Item implements IScribeable {
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide)
            return super.use(level, player, usedHand);

        ItemStack stack = player.getItemInHand(usedHand);
        return HaversackData.fromItemStack(stack).map(data -> {
            if (player.isShiftKeyDown()) {
                if (data.toggle(stack)) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.on"));
                } else {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.off"));
                }
                return InteractionResultHolder.consume(stack);
            }
            ItemStack write = player.getOffhandItem();
            writeStack(player, stack, write);
            return InteractionResultHolder.success(stack);
        }).orElse(InteractionResultHolder.fail(stack));
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

            HaversackData data = new HaversackData(pos, context.getClickedFace(), context.getLevel().dimension(), true, new ArrayList<>());
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
            if (!data.items().isEmpty()) {
                if (data.active()) {
                    tooltip.add(Component.translatable("ars_nouveau.on"));
                } else {
                    tooltip.add(Component.translatable("ars_nouveau.off"));
                }
            }
            for (ItemStack item : data.items()) {
                tooltip.add(item.getHoverName());
            }
        }, () -> {
            tooltip.add(Component.translatable("chat.ars_additions.warp_index.unbound", Component.keybind("key.sneak"), Component.keybind("key.use"), LangUtil.container()));
        });

        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue())) {
            tooltip.add(Component.translatable("tooltip.ars_additions.handy_haversack.instructions"));
        }
    }

    public boolean writeStack(Player player, ItemStack haversack, ItemStack write) {
        return HaversackData.fromItemStack(haversack).map(data -> {
            if (write.isEmpty()) {
                return false;
            }

            if (data.containsStack(write)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                boolean removed = data.remove(write);
                data.write(haversack);
                return removed;
            }
            if (data.add(write)) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                data.write(haversack);
                return true;
            }
            return false;
        }).orElseGet(() -> {
            PortUtil.sendMessage(player, Component.translatable("chat.ars_additions.handy_haversack.invalid"));
            return false;
        });
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack haversack) {
        return writeStack(player, haversack, player.getItemInHand(handIn));
    }

    public static void tryStoreStack(Player player, ItemStack pickedUp, Consumer<ItemStack> remainder) {
        ItemStack haversack = PlayerInvUtil.findItem(player, stack -> stack.is(AddonItemRegistry.HANDY_HAVERSACK.get()), ItemStack.EMPTY, Function.identity());
        if (haversack.isEmpty()) return;

        if (haversack.getItem() instanceof HandyHaversack handyHaversack) {
            HaversackData.fromItemStack(haversack).ifPresent(data -> {
                if (data.containsStack(pickedUp)) {
                    handyHaversack.transportItem(haversack, pickedUp, player, remainder);
                }
            });
        }
    }

    @SubscribeEvent
    public static void entityPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickedUp = event.getItem().getItem();
        tryStoreStack(player, pickedUp, (remainder) -> {
            pickedUp.setCount(remainder.getCount());
            event.setResult(Event.Result.ALLOW);
        });
    }

    @SubscribeEvent
    public static void playerPickup(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickedUp = event.getStack();
        tryStoreStack(player, pickedUp, (remainder) -> {
            pickedUp.setCount(remainder.getCount());
        });
    }

    public record HaversackData(BlockPos pos, Direction side, ResourceKey<Level> level, Boolean active, ArrayList<ItemStack> items) {
        public static final String TAG_KEY = "haversack";

        public static final Codec<HaversackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("BindPos").forGetter(HaversackData::pos),
            Direction.CODEC.optionalFieldOf("BindDirection", Direction.UP).forGetter(HaversackData::side),
            Level.RESOURCE_KEY_CODEC.fieldOf("BindDim").forGetter(HaversackData::level),
            Codec.BOOL.optionalFieldOf("Active", false).forGetter(HaversackData::active),
            ItemStack.CODEC.listOf().optionalFieldOf("Items", List.of()).xmap(Lists::newArrayList, list -> list).forGetter(HaversackData::items)
        ).apply(instance, HaversackData::new));

        public static Optional<HaversackData> fromItemStack(ItemStack stack) {
            return CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().getCompound(TAG_KEY)).result();
        }

        public boolean toggle(ItemStack stack) {
            HaversackData toggled = new HaversackData(pos, side, level, !active, items);
            toggled.write(stack);
            return toggled.active;
        }

        public void write(ItemStack stack) {
            CODEC.encodeStart(NbtOps.INSTANCE, this).result().ifPresent(tag -> {
                stack.getOrCreateTag().put(TAG_KEY, tag);
            });
        }

        public boolean add(ItemStack stack) {
            return items.add(stack.copy());
        }

        public boolean remove(ItemStack stack) {
            return items.removeIf(s -> ItemStack.isSameItem(s, stack));
        }

        public boolean containsStack(ItemStack stack) {
            return items.stream().anyMatch(s -> ItemStack.isSameItem(s, stack));
        }

        @Nullable
        public FilterableItemHandler getItemHandler(Level level) {
            if (!level.isLoaded(pos)) return null;

            BlockEntity be = level.getBlockEntity(pos);
            if (be == null) return null;

            return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side)
                    .map(cap -> new FilterableItemHandler(cap, InvUtil.filtersOnTile(be)))
                    .orElse(null);
        }
    }
}
