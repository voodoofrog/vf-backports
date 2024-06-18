package uk.co.forgottendream.vfbackports.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.predicate.entity.TypeSpecificPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import uk.co.forgottendream.vfbackports.predicate.entity.ModDeserializers;

@Mixin({TypeSpecificPredicate.Deserializers.class})
public abstract class TypeSpecificPredicateMixin {
    @Shadow
    public static BiMap<String, TypeSpecificPredicate.Deserializer> TYPES = new ImmutableBiMap.Builder<String, TypeSpecificPredicate.Deserializer>()
            .put("any", TypeSpecificPredicate.Deserializers.ANY)
            .put("lightning", TypeSpecificPredicate.Deserializers.LIGHTNING)
            .put("fishing_hook", TypeSpecificPredicate.Deserializers.FISHING_HOOK)
            .put("player", TypeSpecificPredicate.Deserializers.PLAYER)
            .put("slime", TypeSpecificPredicate.Deserializers.SLIME)
            .put("cat", TypeSpecificPredicate.Deserializers.CAT.getDeserializer())
            .put("frog", TypeSpecificPredicate.Deserializers.FROG.getDeserializer())
            .put("axolotl", TypeSpecificPredicate.Deserializers.AXOLOTL.getDeserializer())
            .put("boat", TypeSpecificPredicate.Deserializers.BOAT.getDeserializer())
            .put("fox", TypeSpecificPredicate.Deserializers.FOX.getDeserializer())
            .put("mooshroom", TypeSpecificPredicate.Deserializers.MOOSHROOM.getDeserializer())
            .put("painting", TypeSpecificPredicate.Deserializers.PAINTING.getDeserializer())
            .put("rabbit", TypeSpecificPredicate.Deserializers.RABBIT.getDeserializer())
            .put("horse", TypeSpecificPredicate.Deserializers.HORSE.getDeserializer())
            .put("llama", TypeSpecificPredicate.Deserializers.LLAMA.getDeserializer())
            .put("villager", TypeSpecificPredicate.Deserializers.VILLAGER.getDeserializer())
            .put("parrot", TypeSpecificPredicate.Deserializers.PARROT.getDeserializer())
            .put("tropical_fish", TypeSpecificPredicate.Deserializers.TROPICAL_FISH.getDeserializer())
            .put("wolf", ModDeserializers.WOLF.getDeserializer())
            .buildOrThrow();
}
