package mov.oli;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public final class LootTableModifier {

    private static final Identifier IRON_GOLEM_LOOT_TABLE = EntityType.IRON_GOLEM.getLootTableId();

    private static final Identifier RABBIT_LOOT_TABLE = EntityType.RABBIT.getLootTableId();

    private static final Identifier WITHER_SKELETON_LOOT_TABLE = EntityType.WITHER_SKELETON.getLootTableId();

    public static void modify() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && IRON_GOLEM_LOOT_TABLE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(CustomArmorTrims.IRON_GOLEM_HELMET_TRIM));

                tableBuilder.pool(poolBuilder);
            }

            if (source.isBuiltin() && RABBIT_LOOT_TABLE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry
                    .builder(CustomArmorTrims.RABBIT_BOOTS_TRIM))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.2f, 0.025f));

                tableBuilder.pool(poolBuilder);
            }

            if (source.isBuiltin() && WITHER_SKELETON_LOOT_TABLE.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry
                    .builder(CustomArmorTrims.WITHER_SKELETON_CHESTPLATE_TRIM))
                    .conditionally(RandomChanceWithLootingLootCondition.builder(0.5f, 0.01f));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
