package mod.chloeprime.fruitytpr.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Optional;
import java.util.function.Function;

public enum TprAnchorType implements Function<Entity, Vec3>, IExtensibleEnum {
    WORLD_SPAWN(entity -> getServerLevel(entity).map(ServerLevel::getSharedSpawnPos).map(Vec3::atBottomCenterOf).orElse(entity.position())),
    USER_LOCATION(Entity::position);

    public static TprAnchorType create(String name, Function<Entity, Vec3> delegate) {
        throw new AssertionError("Enum not extended");
    }

    private final Function<Entity, Vec3> delegate;

    TprAnchorType(Function<Entity, Vec3> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Vec3 apply(Entity entity) {
        return delegate.apply(entity);
    }

    private static Optional<ServerLevel> getServerLevel(Entity entity) {
        return entity.getLevel() instanceof ServerLevel level ? Optional.of(level) : Optional.empty();
    }
}
