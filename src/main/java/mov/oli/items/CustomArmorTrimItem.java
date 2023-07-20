package mov.oli.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class CustomArmorTrimItem extends Item {

    private final EquipmentSlot slot;

    public CustomArmorTrimItem(Settings settings, EquipmentSlot slot) {
        super(settings);

        this.slot = slot;

        settings.rarity(Rarity.RARE);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }
}
