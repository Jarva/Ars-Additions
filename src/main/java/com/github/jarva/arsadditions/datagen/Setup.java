package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.datagen.client.BlockStateDatagen;
import com.github.jarva.arsadditions.datagen.recipes.LocateStructureProvider;
import com.github.jarva.arsadditions.datagen.recipes.SourceSpawnerProvider;
import com.github.jarva.arsadditions.datagen.tags.BlockTagDatagen;
import com.github.jarva.arsadditions.datagen.tags.EntityTypeTagDatagen;
import com.github.jarva.arsadditions.datagen.tags.ItemTagDatagen;
import com.github.jarva.arsadditions.datagen.worldgen.ProcessorDatagen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.github.jarva.arsadditions.ArsAdditions.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    public static String root = MODID;

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new PatchouliDatagen(gen));
        gen.addProvider(event.includeServer(), new LangDatagen(gen.getPackOutput(), root, "en_us"));
        gen.addProvider(event.includeServer(), new RecipeDatagen(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new EnchantingAppDatagen(gen));
        gen.addProvider(event.includeServer(), new ImbuementDatagen(gen));
        gen.addProvider(event.includeServer(), new DefaultLootDatagen(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new GlyphDatagen(gen));
        gen.addProvider(event.includeServer(), new ProcessorDatagen(gen));
        gen.addProvider(event.includeServer(), new ItemTagDatagen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new BlockTagDatagen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new EntityTypeTagDatagen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new AdvancementDatagen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new BlockStateDatagen(gen.getPackOutput(), event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new LocateStructureProvider(gen));
        gen.addProvider(event.includeServer(), new SourceSpawnerProvider(gen));
    }
}
