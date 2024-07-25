package com.github.jarva.arsadditions.common.ritual;

import com.github.jarva.arsadditions.ArsAdditions;
import com.github.jarva.arsadditions.common.item.data.WayfinderData;
import com.github.jarva.arsadditions.common.recipe.LocateStructureRecipe;
import com.github.jarva.arsadditions.common.util.LangUtil;
import com.github.jarva.arsadditions.server.util.LocateUtil;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonRecipeRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RitualLocateStructure extends AbstractRitual {
    @Nullable private Optional<LocateStructureRecipe> recipe;

    private Optional<LocateStructureRecipe> getRecipe() {
        return AddonRecipeRegistry.LOCATE_STRUCTURE_REGISTRY.getRecipes().stream().filter(r -> r.value().matches(getConsumedItems())).findFirst().map(RecipeHolder::value);
    }

    @Override
    protected void tick() {
        Level world = getWorld();
        if (world == null || getPos() == null) return;

        if (recipe == null) recipe = getRecipe();

        if (recipe.isEmpty()) {
            for (ItemStack consumedItem : getConsumedItems()) {
                dispenseItem(world, consumedItem, getPos());
            }
            setFinished();
            return;
        }

        if (!(world instanceof ServerLevel serverLevel)) return;

        LocateStructureRecipe locator = recipe.get();
        LocateUtil.locate(serverLevel, locator.getStructureHolder(serverLevel), getPos(), locator.getRadius(), locator.getSkipExisting(), (pair) -> {
            if (pair == null) {
                for (ItemStack consumedItem : getConsumedItems()) {
                    dispenseItem(world, consumedItem, getPos());
                }
                setFinished();
                return;
            }

            BlockPos pos = pair.getFirst();
            Holder<Structure> structure = pair.getSecond();
            ItemStack wayfinder = new ItemStack(AddonItemRegistry.WAYFINDER.get(), 1);
            structure.unwrapKey().map(key -> LangUtil.toTitleCase(key.location().getPath())).ifPresent(component -> {
                wayfinder.set(AddonDataComponentRegistry.WAYFINDER_DATA, new WayfinderData(component));
            });
            GlobalPos global = GlobalPos.of(serverLevel.dimension(), pos);
            wayfinder.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(global), false));

            dispenseItem(world, wayfinder, getPos());
            setFinished();
        });
    }

    private void dispenseItem(Level level, ItemStack stack, BlockPos pos) {
        Direction facing = Direction.UP;
        double x = pos.getX() + 0.7 * facing.getStepX();
        double y = pos.getY() + 0.7 * facing.getStepY() - 0.125;
        double z = pos.getZ() + 0.7 * facing.getStepZ();

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
        double g = level.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(level.random.triangle(facing.getStepX() * g, 0.0172275 * 6), level.random.triangle(0.2, 0.0172275 * 6), level.random.triangle(facing.getStepZ() * g, 0.0172275 * 6));
        level.addFreshEntity(itemEntity);
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
    }

    @Override
    public boolean canStart(@Nullable Player player) {
        recipe = getRecipe();
        return recipe.isPresent();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return true;
    }

    @Override
    public String getLangName() {
        return "Locate Structure";
    }

    @Override
    public String getLangDescription() {
        return "Locate a nearby structure based on the augments.";
    }

    public static ResourceLocation RESOURCE_LOCATION = ArsAdditions.prefix("ritual_locate_structure");
    @Override
    public ResourceLocation getRegistryName() {
        return RESOURCE_LOCATION;
    }
}
