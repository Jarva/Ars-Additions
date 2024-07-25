package com.github.jarva.arsadditions.common.util;

import com.hollingsworth.arsnouveau.common.items.ExperienceGem;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DispenserExperienceGemBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(source);
        ItemStack itemStack = stack.split(1);
        spawnItem(source.level(), itemStack, 6, direction, position);
        return stack;
    }

    public static void spawnItem(Level world, ItemStack stack, int speed, Direction direction, Position position) {
        double d = position.x();
        double e = position.y();
        double f = position.z();
        if (direction.getAxis() == Direction.Axis.Y) {
            e -= 0.125;
        } else {
            e -= 0.15625;
        }

        ExperienceGem item = (ExperienceGem) stack.getItem();
        ExperienceOrb entity = new ExperienceOrb(world, d, e, f, item.getValue());
        double g = world.random.nextDouble() * 0.1 + 0.2;
        entity.setDeltaMovement(world.random.triangle((double)direction.getStepX() * g, 0.0172275 * (double)speed), world.random.triangle(0.2, 0.0172275 * (double)speed), world.random.triangle((double)direction.getStepZ() * g, 0.0172275 * (double)speed));
        world.addFreshEntity(entity);
    }
}
