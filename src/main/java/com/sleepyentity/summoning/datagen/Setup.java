package com.sleepyentity.summoning.datagen;

import com.sleepyentity.summoning.SleepySummoningMain;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SleepySummoningMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {

    //use runData configuration to generate stuff, event.includeServer() for data, event.includeClient() for assets
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new ArsProviders.ImbuementProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.GlyphProvider(gen));
        gen.addProvider(event.includeServer(), new ArsProviders.EnchantingAppProvider(gen));

        gen.addProvider(event.includeServer(), new ArsProviders.PatchouliProvider(gen));
    }

}
