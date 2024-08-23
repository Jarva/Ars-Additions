package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.glyph.EffectMark;
import com.github.jarva.arsadditions.common.glyph.MethodRecall;
import com.github.jarva.arsadditions.common.glyph.MethodRetaliate;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.github.jarva.arsadditions.common.ritual.RitualLocateStructure;
import com.github.jarva.arsadditions.setup.registry.recipes.LocateStructureRegistry;
import com.github.jarva.arsadditions.setup.registry.recipes.SourceSpawnerRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.ImbuementRecipeRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ITurretBehavior;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.block.BasicSpellTurret.TURRET_BEHAVIOR_MAP;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> GLYPHS = new ArrayList<>();
    public static List<AbstractRitual> RITUALS = new ArrayList<>();

    static {
        TURRET_BEHAVIOR_MAP.put(MethodRecall.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction) {
                resolver.onCast(null, serverLevel);
            }
        });
    }

    public static void init() {
        registerRituals();
        registerGlyphs();

        LocateStructureRegistry.INSTANCE = new LocateStructureRegistry();
        SourceSpawnerRegistry.INSTANCE = new SourceSpawnerRegistry();
        ImbuementRecipeRegistry.INSTANCE.addRecipeType(AddonRecipeRegistry.CHARM_CHARGING_TYPE);
        ImbuementRecipeRegistry.INSTANCE.addRecipeType(AddonRecipeRegistry.BULK_SCRIBING_TYPE);
        ImbuementRecipeRegistry.INSTANCE.addRecipeType(AddonRecipeRegistry.IMBUE_SCROLL_TYPE);
    }

    private static void registerGlyphs() {
        register(MethodRetaliate.INSTANCE);
        register(EffectMark.INSTANCE);
        register(MethodRecall.INSTANCE);
    }

    private static void registerRituals() {
        register(new RitualChunkLoading());
        register(new RitualLocateStructure());
    }

    private static void register(AbstractRitual ritual) {
        RitualRegistry.registerRitual(ritual);
        RITUALS.add(ritual);
    }

    private static void register(AbstractSpellPart glyph) {
        GlyphRegistry.registerSpell(glyph);
        GLYPHS.add(glyph);
    }
}
