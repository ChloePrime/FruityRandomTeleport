package mod.chloeprime.fruitytpr;

import com.mojang.logging.LogUtils;
import mod.chloeprime.fruitytpr.common.ModContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FruityRandomTeleport.MOD_ID)
public class FruityRandomTeleport {
    public static final String MOD_ID = "fruity_tpr";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final int ONE_HOUR = 20 * 60 * 60;
    public static final int ONE_MINUTE = 20 * 60;
    public static final int FIVE_MINUTES = 20 * 60 * 5;
    public static final String TAG_LAST_TPR_TIME = MOD_ID + ":last_tpr";
    /**
     * 防止掉到熔岩湖里挂掉
     */
    public static final Lazy<MobEffectInstance> LAVA_PROTECTION = Lazy.of(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ONE_MINUTE));
    public static final Lazy<MobEffectInstance> NERF_LAVA_PROTECTION = Lazy.of(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400));
    public static final Lazy<MobEffectInstance> SEA_PROTECTION = Lazy.of(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, FIVE_MINUTES));

    public FruityRandomTeleport() {
        MinecraftForge.EVENT_BUS.register(this);
        ModContents.init0(FMLJavaModLoadingContext.get(), ModLoadingContext.get());
    }
}
