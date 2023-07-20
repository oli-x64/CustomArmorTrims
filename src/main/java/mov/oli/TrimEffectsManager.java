package mov.oli;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrimEffectsManager {

    private static final Map<UUID, BlockPos> bootsPosMap = new HashMap<>();

    private static int tickCounter;

    public static void tick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ScaleData widthData = ScaleTypes.WIDTH.getScaleData(player);
            ScaleData heightData = ScaleTypes.HEIGHT.getScaleData(player);

            Set<String> trims = CustomTrimTracker.getEquippedCustomTrimsFor(player);

            boolean playerScaleSet = false;

            if (trims.contains("customarmortrims:iron_golem")) {
                widthData.setScale(1.25f);
                heightData.setScale(1.25f);

                playerScaleSet = true;

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 80, 1));
            }
            if (trims.contains("customarmortrims:rabbit")) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 80, 1));
            }
            if (trims.contains("customarmortrims:golden_apple")) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 80, 4));
            }
            if (trims.contains("customarmortrims:ultimate_eye")) {
                if (player.getWorld() instanceof ServerWorld serverWorld && serverWorld.getRegistryKey() == World.OVERWORLD) {

                    if (!bootsPosMap.containsKey(player.getUuid())) {
                        bootsPosMap.put(player.getUuid(), null);
                    }

                    // Update the closest stronghold every so often.
                    if (tickCounter % 1200 == 0) {
                        BlockPos strongholdPosition = serverWorld.locateStructure(StructureTags.EYE_OF_ENDER_LOCATED, player.getBlockPos(), 100, false);

                        bootsPosMap.put(player.getUuid(), strongholdPosition);
                    }

                    BlockPos blockPos = bootsPosMap.get(player.getUuid());

                    if (blockPos == null) {
                        player.sendMessage(Text.literal("You do not sense any strongholds nearby...").formatted(Formatting.RED), true);
                    }
                    else {
                        int dx = player.getBlockPos().subtract(blockPos).getX();
                        int dz = player.getBlockPos().subtract(blockPos).getZ();

                        String directionXString = dx > 0 ? "west" : "east";
                        String directionZString = dz > 0 ? "north" : "south";

                        int x = Math.abs(dx);
                        int z = Math.abs(dz);

                        String xSuffix = x == 1 ? "" : "s";
                        String zSuffix = z == 1 ? "" : "s";

                        player.sendMessage(Text.literal("You have a strong urge to travel " +
                                z + " block" + zSuffix + " " + directionZString + ", " +
                                x + " block" + xSuffix + " " + directionXString + "...")
                                .formatted(Formatting.GREEN), true);
                    }
                }
            }

            if (!playerScaleSet) {
                widthData.setScale(1);
                heightData.setScale(1);
            }
        }

        tickCounter++;
    }
}
