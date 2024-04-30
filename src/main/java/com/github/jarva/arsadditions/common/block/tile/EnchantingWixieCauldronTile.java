package com.github.jarva.arsadditions.common.block.tile;

import com.github.jarva.arsadditions.common.recipe.wixie.EnchantingApparatusRecipeWrapper;
import com.github.jarva.arsadditions.setup.registry.AddonBlockRegistry;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;
import software.bernie.ars_nouveau.geckolib3.util.GeckoLibUtil;

public class EnchantingWixieCauldronTile extends WixieCauldronTile implements IAnimatable {
    private static final AnimationBuilder FLOAT = new AnimationBuilder().loop("floating");
    private static final AnimationBuilder CRAFT = new AnimationBuilder().playOnce("enchanting");
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
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

    private PlayState predicate(AnimationEvent<EnchantingWixieCauldronTile> event) {
        if (isCraftingDone()) {
            event.getController().setAnimation(FLOAT);
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(CRAFT);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        controller = new AnimationController<>(this, "Anim", 0, this::predicate);
        animationData.addAnimationController(controller);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
