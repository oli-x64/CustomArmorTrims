package mov.oli;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public final class CustomTrimTracker {

    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    private static final Map<UUID, Set<String>> customArmorTrims = new HashMap<>();

    public static void tick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!customArmorTrims.containsKey(player.getUuid())) {
                customArmorTrims.put(player.getUuid(), new HashSet<>());
            }

            customArmorTrims.get(player.getUuid()).clear();

            for (EquipmentSlot slot : ARMOR_SLOTS) {
                if (!player.hasStackEquipped(slot)) {
                    continue;
                }

                ItemStack stack = player.getEquippedStack(slot);

                NbtCompound trim = stack.getSubNbt("Trim");

                if (stack.hasNbt() && trim != null) {
                    String pattern = trim.get("pattern").asString();

                    if (pattern.startsWith("customarmortrims")) {
                        customArmorTrims.get(player.getUuid()).add(pattern);
                    }
                }
            }
        }
    }

    public static Set<String> getEquippedCustomTrimsFor(PlayerEntity player) {
        if (!customArmorTrims.containsKey(player.getUuid())) {
            customArmorTrims.put(player.getUuid(), new HashSet<>());
        }

        return customArmorTrims.get(player.getUuid());
    }
}
