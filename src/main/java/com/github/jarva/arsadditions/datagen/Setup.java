package com.github.jarva.arsadditions.datagen;

import com.github.jarva.arsadditions.datagen.client.BlockStateDatagen;
import com.github.jarva.arsadditions.datagen.tags.BlockTagDatagen;
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
        gen.addProvider(event.includeServer(), new LangDatagen(gen, root, "en_us"));
        gen.addProvider(event.includeServer(), new RecipeDatagen(gen));
        gen.addProvider(event.includeServer(), new EnchantingAppDatagen(gen));
        gen.addProvider(event.includeServer(), new DefaultLootDatagen(gen));
        gen.addProvider(event.includeServer(), new GlyphDatagen(gen));
        gen.addProvider(event.includeServer(), new ProcessorDatagen(gen));
        BlockTagDatagen btd = new BlockTagDatagen(gen, event.getExistingFileHelper());
        gen.addProvider(event.includeServer(), new ItemTagDatagen(gen, btd, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), btd);
        gen.addProvider(event.includeServer(), new AdvancementDatagen(gen, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new BlockStateDatagen(gen, event.getExistingFileHelper()));
    }
}
