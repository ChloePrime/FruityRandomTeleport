package mod.chloeprime.fruitytpr.common;

import mod.chloeprime.fruitytpr.FruityRandomTeleport;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContents {
    public static class Items {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FruityRandomTeleport.MOD_ID);
        public static final RegistryObject<Item> ENCHANTED_CHORUS_FRUIT = REGISTRY.register("enchanted_chorus_fruit", EnchantedChorusFruit::createMk1);
        public static final RegistryObject<Item> ENCHANTED_CHORUS_FRUIT_MK2 = REGISTRY.register("enchanted_chorus_fruit_mk2", EnchantedChorusFruit::createMk2);

        private Items() {}
    }

    public static void init0(FMLJavaModLoadingContext busStation, ModLoadingContext mlc) {
        mlc.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        Items.REGISTRY.register(busStation.getModEventBus());
        new TprCommand();
    }

    private ModContents() {}
}
