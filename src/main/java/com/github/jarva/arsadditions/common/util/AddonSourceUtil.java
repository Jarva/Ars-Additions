package com.github.jarva.arsadditions.common.util;

import com.github.jarva.arsadditions.common.block.tile.EnderSourceJarTile;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonSourceUtil {
    public static boolean takeSource(BlockPos pos, Level level, int range, int requiredSource) {
        List<ISpecialSourceProvider> availablePositions = SourceUtil.canTakeSource(pos, level, range);
        HashMap<ISpecialSourceProvider, Integer> toTakeFrom = new HashMap<>();

        boolean usingEnderJar = false;
        int neededSource = requiredSource;

        while (neededSource > 0) {
            int remainingSource = Math.min(neededSource, 10_000);
            int foundJars = toTakeFrom.size();
            for (int i = 0; i < availablePositions.size(); i++) {
                ISpecialSourceProvider provider = availablePositions.get(i);
                ISourceTile tile = provider.getSource();

                if (tile instanceof CreativeSourceJarTile) {
                    neededSource = 0;
                    toTakeFrom.clear();
                }

                if (tile instanceof EnderSourceJarTile) {
                    if (usingEnderJar) continue;
                    usingEnderJar = true;
                }

                boolean hasEnough = tile.getSource() >= remainingSource;
                if (hasEnough) {
                    neededSource -= remainingSource;
                    toTakeFrom.put(provider, remainingSource);
                    availablePositions.remove(i);
                    break;
                }
            }

            if (foundJars == toTakeFrom.size()) {
                return false;
            }
        }

        for (Map.Entry<ISpecialSourceProvider, Integer> entry : toTakeFrom.entrySet()) {
            ISpecialSourceProvider provider = entry.getKey();
            Integer source = entry.getValue();
            ISourceTile tile = provider.getSource();
            if (tile.getSource() >= source) {
                tile.removeSource(source);
            } else {
                return false;
            }
        }

        return true;
    }
}
