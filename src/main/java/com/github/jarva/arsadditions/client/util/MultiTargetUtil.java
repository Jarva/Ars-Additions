package com.github.jarva.arsadditions.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class MultiTargetUtil {
    private static final int MAX_BLOCKS = 1000;
    private static final int MAX_ENTITIES = 100;
    private static final double ENTITY_SEARCH_RADIUS = 16.0;

    /**
     * Finds all connected blocks of the same type starting from the given position.
     * Uses breadth-first search to explore adjacent blocks (6-directional).
     * Limited only by MAX_BLOCKS safety limit.
     *
     * @param level The level to search in
     * @param startPos The starting position
     * @return List of all connected block positions (includes startPos)
     */
    public static List<BlockPos> findConnectedBlocks(Level level, BlockPos startPos) {
        BlockState targetState = level.getBlockState(startPos);
        if (targetState.isAir()) {
            return List.of(startPos);
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        List<BlockPos> result = new ArrayList<>();

        toVisit.add(startPos);
        visited.add(startPos);

        while (!toVisit.isEmpty() && result.size() < MAX_BLOCKS) {
            BlockPos current = toVisit.poll();
            result.add(current);

            // Check all 6 adjacent positions
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);

                if (visited.contains(neighbor)) {
                    continue;
                }

                BlockState neighborState = level.getBlockState(neighbor);

                // Check if the block is the same type
                if (neighborState.is(targetState.getBlock())) {
                    visited.add(neighbor);
                    toVisit.add(neighbor);
                }
            }
        }

        return result;
    }

    /**
     * Finds nearby entities of the same type as the starting entity.
     * Recursively chains from each found entity to discover connected groups.
     *
     * @param level The level to search in
     * @param startEntity The starting entity
     * @param radius Search radius in blocks for each entity
     * @return List of all chained entities of same type (includes startEntity)
     */
    public static List<LivingEntity> findNearbyEntities(Level level, LivingEntity startEntity, double radius) {
        EntityType<?> targetType = startEntity.getType();
        Set<LivingEntity> visited = new HashSet<>();
        Queue<LivingEntity> toVisit = new LinkedList<>();
        List<LivingEntity> result = new ArrayList<>();

        toVisit.add(startEntity);
        visited.add(startEntity);

        while (!toVisit.isEmpty() && result.size() < MAX_ENTITIES) {
            LivingEntity current = toVisit.poll();
            result.add(current);

            // Create AABB for search area around current entity
            AABB searchBox = new AABB(
                    current.getX() - radius, current.getY() - radius, current.getZ() - radius,
                    current.getX() + radius, current.getY() + radius, current.getZ() + radius
            );

            // Get all living entities in the search area
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
                    LivingEntity.class,
                    searchBox,
                    entity -> entity.getType() == targetType && !visited.contains(entity)
            );

            // Add new entities to visit queue
            for (LivingEntity entity : nearbyEntities) {
                if (visited.add(entity)) {
                    toVisit.add(entity);
                }
            }
        }

        return result;
    }

    /**
     * Finds nearby entities of the same type with default radius.
     *
     * @param level The level to search in
     * @param startEntity The starting entity
     * @return List of all nearby entities of same type
     */
    public static List<LivingEntity> findNearbyEntities(Level level, LivingEntity startEntity) {
        return findNearbyEntities(level, startEntity, ENTITY_SEARCH_RADIUS);
    }
}
