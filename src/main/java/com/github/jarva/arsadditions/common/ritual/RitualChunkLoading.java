package com.github.jarva.arsadditions.common.ritual;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.setup.config.ServerConfig;
import com.github.jarva.arsadditions.server.storage.ChunkLoadingData;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RitualChunkLoading extends AbstractRitual {
    private List<ChunkPos> chunks = null;

    private UUID activatedPlayer = null;

    private int ticksSinceStart = 0;

    @Override
    protected void tick() {
        Level world = getWorld();

        if (world == null || world.isClientSide || getPos() == null) return;

        if (activatedPlayer == null) {
            BlockPos blockPos = getPos();
            Player nearby = getWorld().getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5, true);
            if (nearby != null) {
                activatedPlayer = nearby.getUUID();
            } else {
                return;
            }
        }

        if (chunks == null) {
            chunks = getChunks();
        }

        if (needsSourceNow()) {
            setChunkLoaded(false);
            return;
        }

        ticksSinceStart++;

        if (ticksSinceStart % 20 == 0) {
            setChunkLoaded(true);
        }

        if (ServerConfig.SERVER.chunkloading_repeat_cost.get() && ticksSinceStart % ServerConfig.SERVER.chunkloading_cost_interval.get() == 0) {
            setNeedsSource(true);
        }
    }

    private List<ChunkPos> getChunks() {
        List<ChunkPos> chunks = new ArrayList<>();
        if (getPos() == null) return chunks;

        int radius = getRadius();

        ChunkPos chunk = new ChunkPos(getPos());
        for (int x = chunk.x - radius; x <= chunk.x + radius; x++) {
            for (int z = chunk.z - radius; z <= chunk.z + radius; z++) {
                chunks.add(new ChunkPos(x, z));
            }
        }

        return chunks;
    }

    public int getRadius() {
        int initialRadius = ServerConfig.SERVER.chunkloading_initial_radius.get();
        if (!ServerConfig.SERVER.chunkloading_radius_incremental.get()) return initialRadius;

        return initialRadius + getConsumedCount();
    }

    public int getConsumedCount() {
        Item configured = getConfiguredItem();
        long consumedCount = getConsumedItems().stream().filter(item -> item.is(configured)).count();

        return (int) Math.min(consumedCount, ServerConfig.SERVER.chunkloading_radius_increment_max.get());
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        if (getWorld() == null) return super.canConsumeItem(stack);
        if (!ServerConfig.SERVER.chunkloading_radius_incremental.get()) return super.canConsumeItem(stack);

        if (getConsumedCount() == ServerConfig.SERVER.chunkloading_radius_increment_max.get()) return super.canConsumeItem(stack);

        Item configured = getConfiguredItem();
        return stack.is(configured);
    }

    public Item getConfiguredItem() {
        if (getWorld() == null) return null;

        ResourceLocation item = ResourceLocation.tryParse(ServerConfig.SERVER.chunkloading_radius_increment_item.get());
        if (item == null) return null;

        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, item);
        Optional<? extends Holder<Item>> optional = getWorld().holderLookup(Registries.ITEM).get(key);
        return optional.map(Holder::value).orElse(null);
    }

    @Override
    public boolean canStart(Player player) {
        Level world = getWorld();
        BlockPos blockPos = getPos();
        if (world == null || blockPos == null) return false;
        if (world.isClientSide) return true;

        if (player == null) {
            Player nearby = getWorld().getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5, false);
            if (nearby != null) {
                activatedPlayer = nearby.getUUID();
            } else {
                return false;
            }
        } else {
            activatedPlayer = player.getUUID();
        }

        return ServerConfig.SERVER.chunkloading_player_limit.get() > ChunkLoadingData.countChunks(world.getServer(), activatedPlayer);
    }

    @Override
    public void onStart(Player player) {
        super.onStart(player);
        setNeedsSource(consumesSource());
    }

    @Override
    public void onStatusChanged(boolean status) {
        setChunkLoaded(status);
    }

    @Override
    public void onDestroy() {
        this.onEnd();
    }

    @Override
    public void onEnd() {
        super.onEnd();
        setChunkLoaded(false);
    }

    @Override
    public boolean consumesSource() {
        return ServerConfig.SERVER.chunkloading_has_cost.get();
    }

    @Override
    public int getSourceCost() {
        return ServerConfig.SERVER.chunkloading_cost.get();
    }

    @Override
    public String getLangName() {
        return "Arcane Permanence";
    }

    @Override
    public String getLangDescription() {
        return "The Ritual of Arcane Permanence force-loads surrounding chunks when provided with a constant stream of source.";
    }

    public static ResourceLocation RESOURCE_LOCATION = ArsAdditions.prefix("ritual_chunk_loading");
    @Override
    public ResourceLocation getRegistryName() {
        return RESOURCE_LOCATION;
    }

    private void setChunkLoaded(boolean shouldLoad) {
        if (getWorld() != null && getWorld() instanceof ServerLevel serverLevel && getPos() != null) {
            if (chunks == null) {
                chunks = getChunks();
            }
            for (ChunkPos chunk : chunks) {
                ChunkLoadingData.updateChunk(serverLevel, activatedPlayer, chunk, shouldLoad);
            }
        }
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        tag.putInt("ticksSinceStart", ticksSinceStart);
        if (activatedPlayer != null) {
            tag.putUUID("activatedPlayer", activatedPlayer);
        }
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        ticksSinceStart = tag.getInt("ticksSinceStart");
        if (tag.contains("activatedPlayer")) {
            activatedPlayer = tag.getUUID("activatedPlayer");
        }
    }
}
