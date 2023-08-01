package mod.chloeprime.fruitytpr.api;

import mod.chloeprime.fruitytpr.FruityRandomTeleport;
import mod.chloeprime.fruitytpr.common.CommonConfig;
import mod.chloeprime.fruitytpr.common.TprAnchorType;
import mod.chloeprime.fruitytpr.mixin.EntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;

import static mod.chloeprime.fruitytpr.FruityRandomTeleport.TAG_LAST_TPR_TIME;

public class FruityRandomTeleportApi {
    private FruityRandomTeleportApi() {}

    /**
     * Those tpr methods will trigger forge events.
     *
     * @param entity the entity that'll be teleported
     * @param minRadius minimum distance between anchor and the position after teleport
     * @param maxRadius maximum distance between anchor and the position after teleport
     * @param anchor origin point of the random teleport eveent
     * @param eventFactory see {@link RandomTeleportEventFactory} for predefined factory instances
     */
    public static boolean tpr(Entity entity, double minRadius, double maxRadius, Vec3 anchor, RandomTeleportEventFactory eventFactory) {
        if (entity.getLevel().isClientSide()) {
            return false;
        }
        var spread = spread(((EntityAccessor) entity).getRandom(), minRadius, maxRadius);
        var targetX = anchor.x + spread.x;
        var targetY = (double) entity.getLevel().getMaxBuildHeight();
        var targetZ = anchor.z + spread.z;

        // 准备 forge 事件
        var event = eventFactory.apply(entity);
        event.setTargetX(targetX);
        event.setTargetY(targetY);
        event.setTargetZ(targetZ);

        // 发送 forge 事件
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        // 传送
        entity.teleportTo(targetX, targetY, targetZ);
        refreshLastTprTime(entity);

        // 给创造模式玩家以外的所有实体添加防摔用的抗性提升X
        if (!(entity instanceof LivingEntity living)) {
            return true;
        }
        if (entity instanceof ServerPlayer player && player.getAbilities().instabuild) {
            player.getAbilities().flying = false;
            return true;
        }
        if (!CommonConfig.NERF_BUFF.get()) {
            living.addEffect(new MobEffectInstance(FruityRandomTeleport.LAVA_PROTECTION.get()));
            living.addEffect(new MobEffectInstance(FruityRandomTeleport.SEA_PROTECTION.get()));
        }
        return true;
    }

    public static boolean tpr(Entity entity, RandomTeleportEventFactory eventFactory) {
        return tpr(entity, CommonConfig.MIN_RADIUS.get(), CommonConfig.MAX_RADIUS.get(), eventFactory);
    }

    public static boolean tpr(Entity entity, double minRadius, double maxRadius, RandomTeleportEventFactory eventFactory) {
        return tpr(entity, minRadius, maxRadius, CommonConfig.ANCHOR_TYPE.get(), eventFactory);
    }

    public static boolean tpr(Entity entity, double minRadius, double maxRadius, TprAnchorType tprAnchorType, RandomTeleportEventFactory eventFactory) {
        return tpr(entity,minRadius,maxRadius,tprAnchorType.apply(entity), eventFactory);
    }

    public static OptionalLong getLastTprTime(Entity entity) {
        var pd = entity.getPersistentData();
        return pd.contains(TAG_LAST_TPR_TIME) ? OptionalLong.of(pd.getLong(TAG_LAST_TPR_TIME)) : OptionalLong.empty();
    }

    public static void setLastTprTime(Entity entity, long gameTime) {
        entity.getPersistentData().putLong(TAG_LAST_TPR_TIME, gameTime);
    }

    public static void refreshLastTprTime(Entity entity) {
        setLastTprTime(entity, entity.getLevel().getGameTime());
    }

    private static Vec3 spread(Random random, double a, double b) {
        int flags = random.nextInt();
        int signX = ((flags & 1) << 1) - 1;
        int signZ = (flags & 2) - 1;
        boolean wideIsX = (flags & 4) == 0;
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double wide = max == 0 ? 0 : random.nextDouble(max);
        double slim = max == min ? min : random.nextDouble(min, max);
        return new Vec3(signX * (wideIsX ? wide : slim), 0, signZ * (wideIsX ? slim : wide));
    }
}
