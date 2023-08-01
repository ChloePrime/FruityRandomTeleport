package mod.chloeprime.fruitytpr.common;

import mod.chloeprime.fruitytpr.api.FruityRandomTeleportApi;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TeleportProtection {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttacked(LivingAttackEvent e) {
        if (e.getSource() != DamageSource.LAVA && e.getSource() != DamageSource.FALL) {
            return;
        }
        FruityRandomTeleportApi.getLastTprTime(e.getEntity()).ifPresent(lastTprTime -> {
            var now = e.getEntity().getLevel().getGameTime();
            if (now <= lastTprTime + CommonConfig.PROTECTION_TIME.get()) {
                e.setCanceled(true);
            }
        });
    }
}
