package mod.chloeprime.fruitytpr.common;

import net.minecraftforge.common.ForgeConfigSpec;

import java.time.Duration;

public class CommonConfig {
    public static final ForgeConfigSpec.DoubleValue MIN_RADIUS;
    public static final ForgeConfigSpec.DoubleValue MAX_RADIUS;
    public static final ForgeConfigSpec.EnumValue<TprAnchorType> ANCHOR_TYPE;
    public static final ForgeConfigSpec.LongValue SHELF_LIFE;
    public static final ForgeConfigSpec.LongValue PROTECTION_TIME;
    public static final ForgeConfigSpec.BooleanValue NERF_BUFF;
    static final ForgeConfigSpec SPEC;

    static {
        var builder = new ForgeConfigSpec.Builder();

        builder.push("Teleportation"); {
            MIN_RADIUS = builder.comment("Minimum radius of random teleport").defineInRange("min_radius", 4096, 0, Double.MAX_VALUE);
            MAX_RADIUS = builder.comment("Maximum radius of random teleport").defineInRange("max_radius", 65536, 0, Double.MAX_VALUE);
            ANCHOR_TYPE = builder
                    .comment("""
                        Center position (anchor) used for random teleport,
                        can be either WORLD_SPAWN or USER_LOCATION"""
                    ).defineEnum("anchor_type", TprAnchorType.WORLD_SPAWN);
        }
        builder.pop();

        builder.push("protection"); {
            PROTECTION_TIME = builder
                    .comment("Time that entity will be immune to fall damage and lava damage, in ticks")
                    .defineInRange("protection_time", 300L, 0, Integer.MAX_VALUE);
            NERF_BUFF = builder
                    .comment("Nerf buff given by normal enchanted chorus fruits and tpr command")
                    .define("nerf_buff", false);
        }
        builder.pop();

        SHELF_LIFE = builder
                .comment("""
                        Shelf life between its creation and its expiry, in milliseconds.
                        set to -1 to disable"""
                ).defineInRange("shelf_life", Duration.ofHours(72).toMillis(), -1, Long.MAX_VALUE);

        SPEC = builder.build();
    }
    private CommonConfig() {}
}
