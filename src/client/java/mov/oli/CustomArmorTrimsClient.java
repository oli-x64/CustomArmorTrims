package mov.oli;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;

import java.util.Optional;

public class CustomArmorTrimsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Replace the vanilla model provider for trimmed items with a custom one.
        ClampedModelPredicateProvider clampedModelPredicateProvider = (stack, world, entity, seed) -> {
            if (!stack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
                return Float.NEGATIVE_INFINITY;
            }
            if (world == null) {
                return 0.0f;
            }

            Optional<ArmorTrim> potentialTrim = ArmorTrim.getTrim(world.getRegistryManager(), stack);

            if(potentialTrim.isEmpty()) {
                return 0;
            }

            ArmorTrim trim = potentialTrim.get();

            if (trim.getPattern().hasKeyAndValue() && trim.getPattern().value().assetId().getNamespace().equals("customarmortrims")) {
                return 0;
            }

            return trim.getMaterial().value().itemModelIndex();
        };

        ModelPredicateProviderRegistry.register(ItemModelGenerator.TRIM_TYPE, clampedModelPredicateProvider);
    }
}
