package com.github.jarva.arsadditions.item;

import com.github.jarva.arsadditions.datagen.tags.ItemTagProvider;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class CodexEntry extends Item {
    public CodexEntry() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel))
            return super.use(level, player, usedHand);

        IPlayerCap playerCap = CapabilityRegistry.getPlayerDataCap(player).orElse(null);
        if (playerCap == null)
            return super.use(level, player, usedHand);

        List<AbstractSpellPart> glyphs = getPossibleGlyphs(playerCap)
                .filter(this::isValidGlyph)
                .toList();

        if (glyphs.isEmpty()) {
            int i = getExpAmount(level.random);
            ExperienceOrb.award(serverLevel, player.position(), i);
            player.sendSystemMessage(Component.translatable("chat.ars_additions.codex_entry.congratulations"));
            player.sendSystemMessage(Component.translatable("chat.ars_additions.codex_entry.no_glyphs"));
        } else {
            AbstractSpellPart glyph = getRandomGlyph(glyphs, level.random);
            playerCap.unlockGlyph(glyph);
            CapabilityRegistry.EventHandler.syncPlayerCap(player);
            player.sendSystemMessage(Component.literal("Unlocked " + glyph.getName()));
        }

        player.getItemInHand(usedHand).shrink(1);
        return super.use(level, player, usedHand);
    }

    public AbstractSpellPart getRandomGlyph(List<AbstractSpellPart> glyphs, RandomSource random) {
        return glyphs.get(random.nextInt(0, glyphs.size()));
    }

    public Stream<AbstractSpellPart> getPossibleGlyphs(IPlayerCap playerCap) {
        List<AbstractSpellPart> startingSpells = GlyphRegistry.getDefaultStartingSpells();

        return GlyphRegistry
                .getSpellpartMap()
                .values()
                .stream()
                .filter(Config::isGlyphEnabled)
                .filter(glyph -> !new ItemStack(glyph.getGlyph()).is(ItemTagProvider.FORGOTTEN_KNOWLEDGE_GLYPHS))
                .filter(glyph -> !startingSpells.contains(glyph))
                .filter(glyph -> !playerCap.knowsGlyph(glyph));
    }

    public int getExpAmount(RandomSource random) {
        return 55;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("chat.ars_additions.codex_entry.lore", getTier().value));
    }

    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    public boolean isValidGlyph(AbstractSpellPart glyph) {
        return glyph.getConfigTier() == getTier();
    }
}
