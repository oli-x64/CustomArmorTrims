package mov.oli.mixin;

import mov.oli.CustomTrimTracker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class RabbitLuckMixin {

    @Inject(at = @At("HEAD"), method = "getLooting", cancellable = true)
    private static void modifyGetLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // If the player has the rabbit trim, then they have looting XII passively on top of normal looting.
        if (CustomTrimTracker.getEquippedCustomTrimsFor(player).contains("customarmortrims:rabbit")) {
            cir.setReturnValue(12 + EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity));
        }
    }
}
