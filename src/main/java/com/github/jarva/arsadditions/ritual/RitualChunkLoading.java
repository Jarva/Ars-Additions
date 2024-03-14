package com.github.jarva.arsadditions.ritual;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class RitualChunkLoading extends AbstractRitual {
    @Override
    protected void tick() {
        Level world = getWorld();

        if (world.isClientSide) return;

        if (needsSourceNow()) {
            setChunkLoaded(false);
            return;
        }

        if (world.getGameTime() % 24000 == 0) {
            setNeedsSource(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setChunkLoaded(true);
    }

    @Override
    public void onEnd() {
        super.onEnd();
        setChunkLoaded(false);
    }

    @Override
    public boolean consumesSource() {
        return true;
    }

    @Override
    public int getSourceCost() {
        return 10000;
    }

    @Override
    public String getLangName() {
        return "Arcane Permanence";
    }

    @Override
    public String getLangDescription() {
        return "The Ritual of Arcane Permanence force-loads surrounding chunks when provided with a constant stream of source.";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsAdditions.MODID, "ritual_chunk_loading");
    }

    private void setChunkLoaded(boolean status) {
        if (getWorld() != null && getWorld() instanceof ServerLevel serverLevel && getPos() != null) {
            serverLevel.getChunkSource().updateChunkForced(new ChunkPos(getPos()), status);
        }
    }
}
