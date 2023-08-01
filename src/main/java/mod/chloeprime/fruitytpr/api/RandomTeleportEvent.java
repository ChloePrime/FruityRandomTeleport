package mod.chloeprime.fruitytpr.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class RandomTeleportEvent extends EntityTeleportEvent {
    protected RandomTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
        super(entity, targetX, targetY, targetZ);
    }

    @Cancelable
    public static class TprCommand extends RandomTeleportEvent {
        /**
         * @see FruityRandomTeleportApi#tpr(Entity, double, double, Vec3, RandomTeleportEventFactory)
         */
        public TprCommand(Entity entity) {
            super(entity, 0, 0, 0);
        }
    }

    @Cancelable
    public static class EnchantedChorusFruit extends RandomTeleportEvent {
        /**
         * @see FruityRandomTeleportApi#tpr(Entity, double, double, Vec3, RandomTeleportEventFactory)
         */
        public EnchantedChorusFruit(Entity entity) {
            super(entity, 0, 0, 0);
        }
    }
}
