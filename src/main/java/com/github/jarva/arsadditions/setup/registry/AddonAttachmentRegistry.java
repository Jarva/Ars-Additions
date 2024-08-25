package com.github.jarva.arsadditions.setup.registry;

import com.github.jarva.arsadditions.ArsAdditions;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AddonAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ArsAdditions.MODID);

    private static final Supplier<AttachmentType<ItemStackHandler>> SINGLE_ITEM_HANDLER = ATTACHMENT_TYPES.register(
            "single_item_handler", () -> AttachmentType.serializable(() -> new ItemStackHandler(1)).build()
    );
    public static final Supplier<AttachmentType<ItemStackHandler>> WARP_NEXUS_INVENTORY = ATTACHMENT_TYPES.register(
            "warp_nexus_inventory", () -> AttachmentType.serializable(() -> new ItemStackHandler(9)).copyOnDeath().build()
    );
    private static final Supplier<AttachmentType<ParticleColor>> PARTICLE_COLOR = ATTACHMENT_TYPES.register(
            "particle_color", () -> AttachmentType.builder(ParticleColor::defaultParticleColor).serialize(ParticleColor.CODEC.codec()).build()
    );
}
