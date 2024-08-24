package com.github.jarva.arsadditions.client.gui;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.networking.TeleportNexusPacket;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WarpNexusScreen extends Screen {

    private final ContainerLevelAccess access;
    private final ItemStackHandler handler;
    private int maxScale;
    private float scaleFactor;
    public int leftStart;
    public int topStart = 0;

    public WarpNexusScreen(ContainerLevelAccess access, ItemStackHandler itemStackHandler) {
        super(Component.literal("Warp Nexus"));
        this.access = access;
        this.handler = itemStackHandler;
    }

    @Override
    protected void init() {
        super.init();
        this.maxScale = this.getMaxAllowedScale();
        this.scaleFactor = 1.0f;

        if (this.minecraft == null) return;

        ClientLevel level = this.minecraft.level;
        if (level == null) return;

        leftStart = (width / 2) - 150;

        int slots = handler.getSlots();

        HashMap<Integer, ItemStack> filled = new HashMap<>();
        for (int i = 0; i < slots; i++) {
            ItemStack scroll = handler.getStackInSlot(i);
            if (scroll.is(Items.AIR)) continue;
            filled.put(i, scroll);
        }

        if (filled.isEmpty()) {
            this.minecraft.setScreen(null);
            PortUtil.sendMessage(this.minecraft.player, Component.translatable("chat.ars_additions.warp_nexus.no_scrolls"));
            PortUtil.sendMessage(this.minecraft.player, Component.translatable("chat.ars_additions.warp_nexus.no_scrolls.instruction", this.minecraft.options.keyShift.getKey().getDisplayName(), this.minecraft.options.keyUse.getKey().getDisplayName()));
            return;
        }

        topStart = (height / 2) - ((8 * 2) + font.lineHeight + (filled.size() * 25) + 20) / 2;
        int index = 0;
        for (Map.Entry<Integer, ItemStack> entry : filled.entrySet()) {
            int i = entry.getKey();
            ItemStack scroll = entry.getValue();
            addRenderableWidget(
                    new Button(leftStart + 50, topStart + (8 * 2) + font.lineHeight + (index * 25), 200, 20, getDisplayName(scroll), (button) -> {
                        access.execute((_level, pos) -> {
                            TeleportNexusPacket.teleport(i, pos);
                        });
                        this.minecraft.setScreen(null);
                    }, Supplier::get) {
                        public static final ResourceLocation BUTTON_LOCATION = ArsAdditions.prefix("textures/gui/button.png");
                        @Override
                        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                            Minecraft minecraft = Minecraft.getInstance();
                            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
                            RenderSystem.enableBlend();
                            RenderSystem.enableDepthTest();
                            guiGraphics.blit(BUTTON_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 16, 4, 200, 20, 0, this.getTextureY());
                            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                            int i = this.getFGColor();
                            this.renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
                        }
                        private int getTextureY() {
                            int i = 0;
                            if (!this.active) {
                                i = 2;
                            } else if (this.isHoveredOrFocused()) {
                                i = 1;
                            }

                            return i * 20;
                        }
                    }
            );

            index++;
        }
    }

    private Component getDisplayName(ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME)) return stack.getHoverName();
        WarpScrollData data = stack.getOrDefault(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true));
        if (!data.isValid()) return stack.getDisplayName();
        BlockPos pos = data.pos().get();
        return Component.translatable("tooltip.ars_additions.reliquary.marked.location", pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (access.evaluate((level, pos) -> !level.getBlockState(pos).is(AddonBlockRegistry.WARP_NEXUS.get()) || this.minecraft.player.blockPosition().distToCenterSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > Math.pow(this.minecraft.player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue(), 2), true)) {
            this.minecraft.setScreen(null);
            return;
        }
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        if (scaleFactor != 1) {
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        renderAfterScale(guiGraphics, mouseX, mouseY, partialTick);
        matrixStack.popPose();
    }

    private int getMaxAllowedScale() {
        return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
    }
    
    public void renderAfterScale(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        if (topStart >= 0) {
            guiGraphics.drawCenteredString(this.font, Component.literal("Warp Nexus"), width / 2, topStart + 8, new Color(255, 255, 255).getRGB());
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
