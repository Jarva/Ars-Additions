package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.common.glyph.EffectMark;
import com.github.jarva.arsadditions.common.glyph.MethodRecall;
import com.github.jarva.arsadditions.common.glyph.MethodRetaliate;
import com.github.jarva.arsadditions.common.ritual.RitualChunkLoading;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ITurretBehavior;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> GLYPHS = new ArrayList<>();
    public static List<AbstractRitual> RITUALS = new ArrayList<>();

    static {
        BasicSpellTurret.TURRET_BEHAVIOR_MAP.put(MethodRecall.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction) {
                resolver.onCast(null, serverLevel);
            }
        });
    }

    public static void init() {
        registerRituals();
        registerGlyphs();
    }

    private static void registerGlyphs() {
        register(MethodRetaliate.INSTANCE);
        register(EffectMark.INSTANCE);
        register(MethodRecall.INSTANCE);
    }

    private static void registerRituals() {
        register(new RitualChunkLoading());
    }

    private static void register(AbstractRitual ritual) {
        ArsNouveauAPI.getInstance().registerRitual(ritual);
        RITUALS.add(ritual);
    }

    private static void register(AbstractSpellPart glyph) {
        ArsNouveauAPI.getInstance().registerSpell(glyph);
        GLYPHS.add(glyph);
    }
}
