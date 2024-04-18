package com.github.jarva.arsadditions.block.tile;

import com.github.jarva.arsadditions.registry.AddonBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WarpNexusTile extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.warp_nexus.new");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public WarpNexusTile(BlockPos pos, BlockState blockState) {
        super(AddonBlockRegistry.WARP_NEXUS_TILE.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, this::idlePredicate));
    }

    private <E extends BlockEntity & GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        return event.setAndContinue(SPIN);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
