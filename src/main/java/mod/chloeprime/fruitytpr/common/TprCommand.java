package mod.chloeprime.fruitytpr.common;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import mod.chloeprime.fruitytpr.api.FruityRandomTeleportApi;
import mod.chloeprime.fruitytpr.api.RandomTeleportEventFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;
import java.util.Collections;

public class TprCommand {
    public TprCommand() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent e) {
        var cmd = e.getDispatcher().register(Commands.literal("tpr").requires(s -> s.hasPermission(2))
                .executes(ctx -> {
                    // /tpr
                    var me = Collections.singletonList(ctx.getSource().getEntityOrException());
                    return tpr(ctx.getSource(), me, defaultAnchor());
                })
                .then(Commands.argument("targets", EntityArgument.entities()).executes(ctx -> {
                    // /tpr @e[type=chicken]
                    var targets = EntityArgument.getEntities(ctx, "targets");
                    return tpr(ctx.getSource(), targets, defaultAnchor());
                }).then(Commands.argument("minRadius", DoubleArgumentType.doubleArg(0)).then(Commands.argument("maxRadius", DoubleArgumentType.doubleArg(0)).executes(ctx -> {
                    // /tpr @e[type=chicken] 4096 65536
                    var targets = EntityArgument.getEntities(ctx, "targets");
                    var minRadius = DoubleArgumentType.getDouble(ctx, "minRadius");
                    var maxRadius = DoubleArgumentType.getDouble(ctx, "maxRadius");
                    return tpr(ctx.getSource(), targets, minRadius, maxRadius, defaultAnchor());
                }).then(Commands.argument("anchor", EnumArgument.enumArgument(TprAnchorType.class)).executes(ctx -> {
                    // /tpr @e[type=chicken] 4096 65536 USER_LOCATION
                    var targets = EntityArgument.getEntities(ctx, "targets");
                    var minRadius = DoubleArgumentType.getDouble(ctx, "minRadius");
                    var maxRadius = DoubleArgumentType.getDouble(ctx, "maxRadius");
                    var anchor = ctx.getArgument("anchor", TprAnchorType.class);
                    return tpr(ctx.getSource(), targets, minRadius, maxRadius, anchor);
                })).then(Commands.argument("anchor_pos", Vec3Argument.vec3()).executes(ctx -> {
                    // /tpr @e[type=chicken] 4096 65536 ~ ~ ~
                    var targets = EntityArgument.getEntities(ctx, "targets");
                    var minRadius = DoubleArgumentType.getDouble(ctx, "minRadius");
                    var maxRadius = DoubleArgumentType.getDouble(ctx, "maxRadius");
                    var anchor = Vec3Argument.getCoordinates(ctx, "anchor_pos");
                    return tpr(ctx.getSource(), targets, minRadius, maxRadius, anchor);
                }))))));
    }

    private static int tpr(CommandSourceStack source, Collection<? extends Entity> targets, TprAnchorType anchor) {
        return tpr(source, targets, CommonConfig.MIN_RADIUS.get(), CommonConfig.MAX_RADIUS.get(), anchor);
    }

    private static int tpr(CommandSourceStack source, Collection<? extends Entity> targets, double minRadius, double maxRadius, TprAnchorType anchor) {
        int success = 0;
        for(Entity entity : targets) {
            success += FruityRandomTeleportApi.tpr(entity, minRadius, maxRadius, anchor, RandomTeleportEventFactory.TPR_COMMAND) ? 1 : 0;
        }
        return afterTpr(source, targets, success);
    }

    private static int tpr(CommandSourceStack source, Collection<? extends Entity> targets, double minRadius, double maxRadius, Coordinates coordinates) {
        Vec3 pos = coordinates.getPosition(source);

        int success = 0;
        for(Entity entity : targets) {
            success += FruityRandomTeleportApi.tpr(entity, minRadius, maxRadius, pos, RandomTeleportEventFactory.TPR_COMMAND) ? 1 : 0;
        }

        return afterTpr(source, targets, success);
    }

    private static int afterTpr(CommandSourceStack source, Collection<? extends Entity> targets, int success) {
        if (targets.size() == 1) {
            var name = targets.iterator().next().getDisplayName();
            if (success == 1) {
                source.sendSuccess(new TranslatableComponent("commands.fruity_tpr.tpr.success.single", name), true);
            } else {
                source.sendFailure(new TranslatableComponent("commands.fruity_tpr.tpr.failure.single", name));
            }
        } else {
            source.sendSuccess(new TranslatableComponent("commands.fruity_tpr.tpr.multiple", targets.size(), success, targets.size() - success), true);
        }
        return success;
    }

    private static TprAnchorType defaultAnchor() {
        return CommonConfig.ANCHOR_TYPE.get();
    }
}
