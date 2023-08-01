package mod.chloeprime.fruitytpr.api;

import net.minecraft.world.entity.Entity;

import java.util.function.Function;

@FunctionalInterface
public interface RandomTeleportEventFactory extends Function<Entity, RandomTeleportEvent> {
    RandomTeleportEventFactory TPR_COMMAND = RandomTeleportEvent.TprCommand::new;
    RandomTeleportEventFactory ENCHANTED_CHORUS_FRUIT = RandomTeleportEvent.EnchantedChorusFruit::new;
}
