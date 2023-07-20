package mov.oli;

import mov.oli.items.CustomArmorTrimItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSwingHandEvent;

public class CustomArmorTrims implements ModInitializer {

    public static final Item IRON_GOLEM_HELMET_TRIM = new CustomArmorTrimItem(new FabricItemSettings(), EquipmentSlot.HEAD);

    public static final Item RABBIT_BOOTS_TRIM = new CustomArmorTrimItem(new FabricItemSettings(), EquipmentSlot.FEET);

    public static final Item GOLDEN_APPLE_LEGGINGS_TRIM = new CustomArmorTrimItem(new FabricItemSettings(), EquipmentSlot.LEGS);

    public static final Item WITHER_SKELETON_CHESTPLATE_TRIM = new CustomArmorTrimItem(new FabricItemSettings(), EquipmentSlot.CHEST);

    public static final Item ULTIMATE_EYE_BOOTS_TRIM = new CustomArmorTrimItem(new FabricItemSettings(), EquipmentSlot.FEET);

    private static final ItemGroup TRIM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
            .displayName(Text.translatable("itemGroup.customarmortrims.trims"))
            .entries((displayContext, entries) -> {
                entries.add(IRON_GOLEM_HELMET_TRIM);
                entries.add(RABBIT_BOOTS_TRIM);
                entries.add(GOLDEN_APPLE_LEGGINGS_TRIM);
                entries.add(WITHER_SKELETON_CHESTPLATE_TRIM);
                entries.add(ULTIMATE_EYE_BOOTS_TRIM);
            })
            .build();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier("customarmortrims", "iron_golem_helmet_trim"), IRON_GOLEM_HELMET_TRIM);
        Registry.register(Registries.ITEM, new Identifier("customarmortrims", "rabbit_boots_trim"), RABBIT_BOOTS_TRIM);
        Registry.register(Registries.ITEM, new Identifier("customarmortrims", "golden_apple_leggings_trim"), GOLDEN_APPLE_LEGGINGS_TRIM);
        Registry.register(Registries.ITEM, new Identifier("customarmortrims", "wither_skeleton_chestplate_trim"), WITHER_SKELETON_CHESTPLATE_TRIM);
        Registry.register(Registries.ITEM, new Identifier("customarmortrims", "ultimate_eye_boots_trim"), ULTIMATE_EYE_BOOTS_TRIM);

        Registry.register(Registries.ITEM_GROUP, new Identifier("customarmortrims", "trim_group"), TRIM_GROUP);

        LootTableModifier.modify();

        ServerTickEvents.START_SERVER_TICK.register(CustomTrimTracker::tick);
        ServerTickEvents.START_SERVER_TICK.register(TrimEffectsManager::tick);

        initialiseStimuliEvents();
    }

    private void initialiseStimuliEvents() {
        Stimuli.global().listen(BlockBreakEvent.EVENT, (player, world, pos) -> {

            if (CustomTrimTracker.getEquippedCustomTrimsFor(player).contains("customarmortrims:iron_golem")) {
                for (int x = -1; x < 2; x++) {
                    for (int y = -1; y < 2; y++) {
                        for (int z = -1; z < 2; z++) {
                            if (x == 0 && y == 0 && z == 0) {
                                continue;
                            }

                            BlockPos offset = pos.add(x, y, z);

                            if (!player.isBlockBreakingRestricted(world, offset, player.interactionManager.getGameMode())) {
                                world.breakBlock(offset, !player.isCreative());
                            }
                        }
                    }
                }
            }

            return ActionResult.PASS;
        });

        Stimuli.global().listen(BlockDropItemsEvent.EVENT, (breaker, world, pos, state, dropStacks) -> {

            if (breaker instanceof PlayerEntity player && CustomTrimTracker.getEquippedCustomTrimsFor(player).contains("customarmortrims:rabbit")) {
                for (ItemStack itemStack : dropStacks) {
                    int amount = itemStack.getCount();

                    itemStack.setCount(amount * 5);

                    if (itemStack.getCount() > itemStack.getMaxCount()) {
                        itemStack.setCount(itemStack.getMaxCount());
                    }
                }
            }

            return TypedActionResult.pass(dropStacks);
        });

        Stimuli.global().listen(PlayerDamageEvent.EVENT, (player, source, amount) -> {

            if (CustomTrimTracker.getEquippedCustomTrimsFor(player).contains("customarmortrims:golden_apple")) {
                if (source.isOf(DamageTypes.FALL)) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });

        Stimuli.global().listen(PlayerSwingHandEvent.EVENT, (player, hand) -> {
            if (CustomTrimTracker.getEquippedCustomTrimsFor(player).contains("customarmortrims:wither_skeleton")) {
                if (player.isSneaking()) {

                    if (!player.getStackInHand(hand).isEmpty()) {
                        return;
                    }

                    if (player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand).getItem())) {
                        return;
                    }

                    Vec3d lookVector = Vec3d.fromPolar(player.getPitch(), player.getHeadYaw()).normalize();
                    Vec3d skullPosition = player.getEyePos().add(lookVector);

                     WitherSkullEntity witherSkull = new WitherSkullEntity(player.getWorld(), player,
                        lookVector.x, lookVector.y, lookVector.z);

                    witherSkull.setCharged(true);
                    witherSkull.setOwner(player);
                    witherSkull.setPos(skullPosition.getX(), skullPosition.getY(), skullPosition.getZ());

                    player.getWorld().spawnEntity(witherSkull);

                    player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), 12);
                }
            }
        });
    }
}