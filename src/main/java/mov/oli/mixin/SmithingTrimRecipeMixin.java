package mov.oli.mixin;

import mov.oli.items.CustomArmorTrimItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public class SmithingTrimRecipeMixin {

    @Inject(at = @At("HEAD"), method = "matches", cancellable = true)
    public void recipeMatches(Inventory inventory, World world, CallbackInfoReturnable<Boolean> cir) {
        if (inventory.getStack(1).isEmpty()) {
            return;
        }

        EquipmentSlot armorEquipmentSlot = ((ArmorItem)inventory.getStack(1).getItem()).getSlotType();

        // Make sure custom trims match their armor piece.
        if (inventory.getStack(0).getItem() instanceof CustomArmorTrimItem customArmorTrimItem &&
                customArmorTrimItem.getSlot() != armorEquipmentSlot) {

            cir.setReturnValue(false);
        }
    }
}
