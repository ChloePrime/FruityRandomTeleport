package mod.chloeprime.fruitytpr.common;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnchantedChorusFruit extends SimpleFoiledItem {
    public EnchantedChorusFruit(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        ItemStack stack = super.finishUsingItem(pStack, pLevel, pLivingEntity);

        if (!pLevel.isClientSide) {

        }

        return stack;
    }

    public static EnchantedChorusFruit create() {
        return new EnchantedChorusFruit((new Item.Properties())
                .tab(CreativeModeTab.TAB_FOOD)
                .rarity(Rarity.UNCOMMON)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(2)
                        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 9), 1)
                        .build())
        );
    }
}
