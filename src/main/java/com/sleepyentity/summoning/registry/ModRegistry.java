package com.sleepyentity.summoning.registry;

import com.sleepyentity.summoning.glyphs.EffectSummonImp;
import com.sleepyentity.summoning.item.ExampleCosmetic;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.sleepyentity.summoning.SleepySummoningMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SleepySummoningMain.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SleepySummoningMain.MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SleepySummoningMain.MODID);


    public static void registerRegistries(IEventBus bus){
        BLOCKS.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
    }

    public static final RegistryObject<Item> EXAMPLE;

    //this is an example of how to register a sound. You also need to add the sound to the sound.json file, referencing your ogg files, and a texture for the button under textures/sounds.
    //this example will use one of the existing sounds randomly
    public static RegistryObject<SoundEvent> EXAMPLE_FAMILY = SOUNDS.register("example_sound", () -> makeSound("example_sound"));
    public static SpellSound EXAMPLE_SPELL_SOUND;


    static {
        EXAMPLE = ITEMS.register("star_hat", () -> new ExampleCosmetic(new Item.Properties().tab(ArsNouveau.itemGroup)));
    }

    static SoundEvent makeSound(String name) {
        return new SoundEvent(new ResourceLocation(SleepySummoningMain.MODID, name));
    }

}
