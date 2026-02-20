package com.github.jarva.arsadditions.common.item;

import com.github.jarva.arsadditions.common.item.data.MemoryCrystalData;
import com.github.jarva.arsadditions.common.memory.MemoryHandler;
import com.github.jarva.arsadditions.common.memory.MemoryHandlerRegistry;
import com.github.jarva.arsadditions.mixin.GuiAccessor;
import com.github.jarva.arsadditions.setup.networking.NetworkHandler;
import com.github.jarva.arsadditions.setup.networking.PacketMemoryCrystalAction;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MemoryCrystal extends Item implements IRadialProvider {

    public static final String NBT_HANDLER_ID = "HandlerID";
    public static final int CLEAR_SLOT_INDEX = MemoryCrystalData.MAX_SLOTS;
    public static final int LOCK_SLOT_INDEX = MemoryCrystalData.MAX_SLOTS + 1;
    private static final int OVERLAY_MESSAGE_TICKS = 20;

    public MemoryCrystal() {
        super(AddonItemRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide || !isSelected) {
            return;
        }

        if (!(entity instanceof Player player)) {
            return;
        }

        showActionBarStatus(stack, player);
    }

    @OnlyIn(Dist.CLIENT)
    private void showActionBarStatus(ItemStack stack, Player player) {
        MemoryCrystalData data = MemoryCrystalData.fromItemStack(stack);
        MemoryCrystalData.MemorySlot slot = data.slots().get(data.selectedSlot());

        Component message = Component.literal((data.selectedSlot() + 1) + " | " + formatSlotDescription(slot));

        GuiAccessor gui = (GuiAccessor) Minecraft.getInstance().gui;
        gui.setOverlayMessageString(message);
        gui.setOverlayMessageTime(OVERLAY_MESSAGE_TICKS);
    }

    public static ItemStack getCrystalFromHand(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof MemoryCrystal) {
            return mainHand;
        }
        return player.getOffhandItem();
    }

    public static boolean isHoldingCrystal(Player player) {
        return getCrystalFromHand(player).getItem() instanceof MemoryCrystal;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        if (Screen.hasShiftDown()) {
            Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getActionMenuProvider(stack, player)));
        } else {
            Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getSlotSelectionMenuProvider(stack, player)));
        }
    }

    public RadialMenu<ItemStack> getSlotSelectionMenuProvider(ItemStack stack, Player player) {
        MemoryCrystalData data = MemoryCrystalData.fromItemStack(getCrystalFromHand(player));
        List<RadialMenuSlot<ItemStack>> slots = new ArrayList<>();

        for (int i = 0; i < MemoryCrystalData.MAX_SLOTS; i++) {
            slots.add(createSlotMenuEntry(data, i));
        }

        return new RadialMenu<>(
            (int index) -> NetworkHandler.sendToServer(new PacketMemoryCrystalAction(index)),
            slots,
            RenderUtils::drawItemAsIcon,
            0
        );
    }

    public RadialMenu<ItemStack> getActionMenuProvider(ItemStack stack, Player player) {
        MemoryCrystalData data = MemoryCrystalData.fromItemStack(getCrystalFromHand(player));
        List<RadialMenuSlot<ItemStack>> slots = new ArrayList<>();

        slots.add(new RadialMenuSlot<>(
            Component.translatable("tooltip.ars_additions.memory_crystal.radial.clear").getString(),
            new ItemStack(Items.BARRIER)
        ));

        String lockText = data.isSelectedLocked()
            ? Component.translatable("tooltip.ars_additions.memory_crystal.radial.unlock").getString()
            : Component.translatable("tooltip.ars_additions.memory_crystal.radial.lock").getString();
        slots.add(new RadialMenuSlot<>(lockText, new ItemStack(Items.TRIPWIRE_HOOK)));

        int[] actionMapping = { CLEAR_SLOT_INDEX, LOCK_SLOT_INDEX };

        return new RadialMenu<>(
            (int index) -> NetworkHandler.sendToServer(new PacketMemoryCrystalAction(actionMapping[index])),
            slots,
            RenderUtils::drawItemAsIcon,
            0
        );
    }

    private RadialMenuSlot<ItemStack> createSlotMenuEntry(MemoryCrystalData data, int slotIndex) {
        MemoryCrystalData.MemorySlot slot = data.slots().get(slotIndex);
        String prefix = data.selectedSlot() == slotIndex ? ChatFormatting.GOLD + "▶ " : "";
        String description = prefix + formatSlotDescription(slot);

        ItemStack icon = slot.data()
            .flatMap(this::getHandlerFromData)
            .map(MemoryHandler::getIcon)
            .orElseGet(() -> slot.data().isPresent() ? new ItemStack(Items.BARRIER) : ItemStack.EMPTY);

        return new RadialMenuSlot<>(description, icon);
    }

    private String formatSlotDescription(MemoryCrystalData.MemorySlot slot) {
        String lockIndicator = slot.locked() ? ChatFormatting.YELLOW + "🔒 " : "";

        return slot.data()
            .map(tag -> getHandlerFromData(tag)
                .map(handler -> lockIndicator + ChatFormatting.GREEN + handler.getDisplayName().getString())
                .orElseGet(() -> lockIndicator + ChatFormatting.RED +
                    Component.translatable(tag.contains(NBT_HANDLER_ID)
                        ? "tooltip.ars_additions.memory_crystal.radial.unknown_handler"
                        : "tooltip.ars_additions.memory_crystal.radial.corrupt_data").getString()))
            .orElseGet(() -> lockIndicator + ChatFormatting.GRAY +
                Component.translatable("tooltip.ars_additions.memory_crystal.radial.empty").getString());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
        if (blockEntity == null) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        MemoryCrystalData data = MemoryCrystalData.fromItemStack(stack);

        if (data.hasData()) {
            loadToBlock(player, stack, blockEntity, data);
        } else {
            saveFromBlock(player, stack, blockEntity, data);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interactLivingEntity(
        ItemStack stack, Player player, LivingEntity target, InteractionHand hand
    ) {
        if (!player.isShiftKeyDown()) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        if (!(player.level() instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        MemoryCrystalData data = MemoryCrystalData.fromItemStack(stack);

        if (data.hasData()) {
            loadToEntity(player, stack, target, data);
        } else {
            saveFromEntity(player, stack, target, data);
        }

        return InteractionResult.SUCCESS;
    }

    private void saveFromBlock(Player player, ItemStack stack, BlockEntity blockEntity, MemoryCrystalData data) {
        if (data.isSelectedLocked()) {
            sendError(player, "chat.ars_additions.memory_crystal.slot_locked");
            return;
        }

        findHandler(h -> h.canSaveFrom(blockEntity)).ifPresentOrElse(
            handler -> saveData(player, stack, handler.save(blockEntity), handler),
            () -> sendError(player, "chat.ars_additions.memory_crystal.unsupported_block")
        );
    }

    private void saveFromEntity(Player player, ItemStack stack, Entity entity, MemoryCrystalData data) {
        if (data.isSelectedLocked()) {
            sendError(player, "chat.ars_additions.memory_crystal.slot_locked");
            return;
        }

        findHandler(h -> h.canSaveFrom(entity)).ifPresentOrElse(
            handler -> saveData(player, stack, handler.save(entity), handler),
            () -> sendError(player, "chat.ars_additions.memory_crystal.unsupported_entity")
        );
    }

    private void saveData(Player player, ItemStack stack, CompoundTag data, MemoryHandler handler) {
        data.putString(NBT_HANDLER_ID, handler.getId().toString());

        MemoryCrystalData crystalData = MemoryCrystalData.fromItemStack(stack).withData(data);
        crystalData.write(stack);

        sendSuccess(player, "chat.ars_additions.memory_crystal.saved", crystalData.selectedSlot() + 1);
    }

    private void loadToBlock(Player player, ItemStack stack, BlockEntity blockEntity, MemoryCrystalData data) {
        findHandler(h -> h.canLoadTo(blockEntity)).ifPresentOrElse(
            handler -> {
                handler.load(blockEntity, data.getSelectedData().get(), player);
                sendSuccess(player, "chat.ars_additions.memory_crystal.loaded", data.selectedSlot() + 1);
            },
            () -> sendError(player, "chat.ars_additions.memory_crystal.unsupported_block")
        );
    }

    private void loadToEntity(Player player, ItemStack stack, Entity entity, MemoryCrystalData data) {
        findHandler(h -> h.canLoadTo(entity)).ifPresentOrElse(
            handler -> {
                handler.load(entity, data.getSelectedData().get(), player);
                sendSuccess(player, "chat.ars_additions.memory_crystal.loaded", data.selectedSlot() + 1);
            },
            () -> sendError(player, "chat.ars_additions.memory_crystal.unsupported_entity")
        );
    }

    private Optional<MemoryHandler> findHandler(Predicate<MemoryHandler> predicate) {
        return MemoryHandlerRegistry.getAllHandlers().stream()
            .filter(predicate)
            .findFirst();
    }

    private void sendError(Player player, String key, Object... args) {
        PortUtil.sendMessage(player,
            Component.translatable(key, args).withStyle(ChatFormatting.RED));
    }

    private void sendSuccess(Player player, String key, Object... args) {
        PortUtil.sendMessage(player,
            Component.translatable(key, args).withStyle(ChatFormatting.GREEN));
    }

    @Override
    public void appendHoverText(
        ItemStack stack, TooltipContext context,
        List<Component> tooltip, TooltipFlag flag
    ) {
        MemoryCrystalData data = MemoryCrystalData.fromItemStack(stack);

        String lockStatus = data.isSelectedLocked() ? " 🔒" : "";
        tooltip.add(Component.translatable(
            "tooltip.ars_additions.memory_crystal.selected_slot",
            data.selectedSlot() + 1
        ).append(lockStatus).withStyle(ChatFormatting.GOLD));

        data.getSelectedData().ifPresentOrElse(
            slotData -> getHandlerFromData(slotData).ifPresentOrElse(
                handler -> {
                    tooltip.add(handler.getDisplayName().copy().withStyle(ChatFormatting.GREEN));
                    handler.getTooltip(slotData, tooltip);
                },
                () -> tooltip.add(Component.translatable(
                    "tooltip.ars_additions.memory_crystal.slot_has_data"
                ).withStyle(ChatFormatting.GREEN))
            ),
            () -> tooltip.add(Component.translatable(
                "tooltip.ars_additions.memory_crystal.radial.empty"
            ).withStyle(ChatFormatting.GRAY))
        );

        tooltip.add(Component.translatable(
            "tooltip.ars_additions.memory_crystal.usage"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }

    private Optional<MemoryHandler> getHandlerFromData(CompoundTag data) {
        return Optional.of(data)
            .filter(tag -> tag.contains(NBT_HANDLER_ID))
            .flatMap(tag -> Optional.ofNullable(ResourceLocation.tryParse(tag.getString(NBT_HANDLER_ID))))
            .map(MemoryHandlerRegistry::get)
            .flatMap(Optional::ofNullable);
    }
}
