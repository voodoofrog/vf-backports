package uk.co.forgottendream.vfbackports.predicate.entity;

import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import net.minecraft.predicate.entity.VariantPredicates;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;

import java.util.Optional;

public final class ModDeserializers {
    public static final VariantPredicates<WolfVariant> WOLF = VariantPredicates.create(ModRegistries.WOLF_VARIANTS, (entity) -> {
        Optional<WolfVariant> variant;
        if (entity instanceof WolfEntity wolfEntity) {
            variant = Optional.of(((VariantHolder<WolfVariant>) wolfEntity).getVariant());
        } else {
            variant = Optional.empty();
        }

        return variant;
    });

    public static TypeSpecificPredicate wolf(WolfVariant variant) {
        return ModDeserializers.WOLF.createPredicate(variant);
    }
}
