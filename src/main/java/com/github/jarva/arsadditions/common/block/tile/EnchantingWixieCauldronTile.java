package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.common.recipe.wixie.EnchantingApparatusRecipeWrapper;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

public class EnchantingWixieCauldronTile extends WixieCauldronTile implements GeoBlockEntity {
    private static final RawAnimation FLOAT = RawAnimation.begin().thenLoop("floating");
    private static final RawAnimation CRAFT = RawAnimation.begin().thenPlay("enchanting");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public AnimationController<EnchantingWixieCauldronTile> controller;

    public EnchantingWixieCauldronTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return AddonBlockRegistry.WIXIE_ENCHANTING_TILE.get();
    }

    @Override
    public MultiRecipeWrapper getRecipesForStack(ItemStack stack) {
        return EnchantingApparatusRecipeWrapper.fromStack(stack, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controller = new AnimationController<>(this, this::predicate);
        controllerRegistrar.add(controller);
    }

    private <E extends BlockEntity & GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (isCraftingDone()) {
            return event.setAndContinue(FLOAT);
        }
        return event.setAndContinue(CRAFT);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
