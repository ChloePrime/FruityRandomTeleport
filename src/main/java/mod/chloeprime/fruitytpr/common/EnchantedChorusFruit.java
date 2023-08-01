package mod.chloeprime.fruitytpr.common;

import mod.chloeprime.fruitytpr.FruityRandomTeleport;
import mod.chloeprime.fruitytpr.api.FruityRandomTeleportApi;
import mod.chloeprime.fruitytpr.api.RandomTeleportEventFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnchantedChorusFruit extends SimpleFoiledItem {
    public static final String TAG_UNEXPIRABLE = "Unexpirable";
    public static final String TAG_PRODUCTION_DATE = "mfd";
    public static final DateTimeFormatter MFD_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    public EnchantedChorusFruit(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pLivingEntity);

        if (!pLevel.isClientSide) {
            FruityRandomTeleportApi.tpr(pLivingEntity, RandomTeleportEventFactory.ENCHANTED_CHORUS_FRUIT);
        }

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        var shelfLife = CommonConfig.SHELF_LIFE.get();
        if (shelfLife != -1 && stack.hasTag()) {
            var tag = requireNonNull(stack.getTag());
            if (tag.getBoolean(TAG_UNEXPIRABLE)) {
                list.add(new TranslatableComponent("item.fruity_tpr.enchanted_chorus_fruit.unexpirable").withStyle(ChatFormatting.DARK_PURPLE));
            } else {
                var mfdMillis = tag.getLong(TAG_PRODUCTION_DATE);
                var timeZone = ZoneId.systemDefault();
                var mfd = ZonedDateTime.ofInstant(Instant.ofEpochMilli(mfdMillis), timeZone);
                var exp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(mfdMillis + shelfLife), timeZone);
                list.add(new TranslatableComponent("item.fruity_tpr.enchanted_chorus_fruit.mfd", mfd.format(MFD_FORMAT)).withStyle(ChatFormatting.GRAY));
                list.add(new TranslatableComponent("item.fruity_tpr.enchanted_chorus_fruit.exp", exp.format(MFD_FORMAT)).withStyle(ChatFormatting.GRAY));
            }
        }
        super.appendHoverText(stack, pLevel, list, pIsAdvanced);
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent e) {
        var shelfLife = CommonConfig.SHELF_LIFE.get();
        if (shelfLife == -1) {
            return;
        }
        if (e.getCrafting().getItem() instanceof EnchantedChorusFruit) {
            tickChorusFruit(e.getCrafting(), shelfLife);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END || e.player.getLevel().isClientSide() || !(e.player instanceof ServerPlayer player)) {
            return;
        }
        // 创造模式下不会触发保质期初始化和过期判定
        if (player.getAbilities().instabuild) {
            return;
        }
        // 降低tick频率，避免高频执行
        if (player.getLevel().getGameTime() % 7 != 0) {
            return;
        }
        var shelfLife = CommonConfig.SHELF_LIFE.get();
        if (shelfLife == -1) {
            return;
        }
        tickCarried(player, shelfLife);
        tickInventory(player, shelfLife);
    }

    private static void tickCarried(ServerPlayer player, long shelfLife) {
        var carried = player.containerMenu.getCarried();
        if (!(carried.getItem() instanceof EnchantedChorusFruit)) {
            return;
        }
        var after = tickChorusFruit(carried, shelfLife);
        if (after == carried) {
            return;
        }
        player.containerMenu.setCarried(after);
    }

    private static void tickInventory(ServerPlayer player, long shelfLife) {
        var inv = player.getInventory();
        var count = inv.getContainerSize();
        for (int i = 0; i < count; i++) {
            var fruit = inv.getItem(i);
            if (!(fruit.getItem() instanceof EnchantedChorusFruit)) {
                continue;
            }
            var after = tickChorusFruit(fruit, shelfLife);
            if (after == fruit) {
                continue;
            }
            inv.setItem(i, after);
        }
    }

    private static ItemStack tickChorusFruit(ItemStack fruit, long shelfLife) {
        // Unexpirable 标签会让紫颂果永远不会过期
        if (fruit.hasTag() && requireNonNull(fruit.getTag()).getBoolean(TAG_UNEXPIRABLE)) {
            return fruit;
        }

        var now = System.currentTimeMillis();

        if (!fruit.hasTag() || !requireNonNull(fruit.getTag()).contains(TAG_PRODUCTION_DATE)) {
            fruit.getOrCreateTag().putLong(TAG_PRODUCTION_DATE, now);
            return fruit;
        }

        var tag = fruit.getOrCreateTag();
        var productionDate = tag.getLong(TAG_PRODUCTION_DATE);

        // 没有过期，那么返回原物品
        if (now <= productionDate + shelfLife) {
            return fruit;
        }

        // 删除保质期信息，变成保留其他tag的普通紫颂果
        tag.remove(TAG_PRODUCTION_DATE);

        var normalChorusFruit = Items.CHORUS_FRUIT.getDefaultInstance();
        normalChorusFruit.setCount(fruit.getCount());
        normalChorusFruit.setTag(tag);

        return normalChorusFruit;
    }

    public static EnchantedChorusFruit createMk1() {
        return new EnchantedChorusFruit((new Item.Properties())
                .tab(CreativeModeTab.TAB_FOOD)
                .rarity(Rarity.UNCOMMON)
                .food((new FoodProperties.Builder())
                        .alwaysEat()
                        .nutrition(4)
                        .saturationMod(1.5F)
                        .build())
        );
    }

    public static EnchantedChorusFruit createMk2() {
        return new EnchantedChorusFruit((new Item.Properties())
                .tab(CreativeModeTab.TAB_FOOD)
                .rarity(Rarity.EPIC)
                .food((new FoodProperties.Builder())
                        .alwaysEat()
                        .nutrition(4)
                        .saturationMod(2)
                        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, FruityRandomTeleport.ONE_HOUR, 9), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FruityRandomTeleport.FIVE_MINUTES), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, FruityRandomTeleport.FIVE_MINUTES), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.SATURATION, FruityRandomTeleport.ONE_HOUR), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.HEAL, FruityRandomTeleport.ONE_HOUR), 1)
                        .build())
        );
    }
}
