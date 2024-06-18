package uk.co.forgottendream.vfbackports.mixin;

import net.minecraft.data.server.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;

import java.util.List;

@Mixin({VanillaHusbandryTabAdvancementGenerator.class})
public class VanillaHusbandryTabAdvancementGeneratorMixin {
    @Shadow
    public static List<EntityType<?>> BREEDABLE_ANIMALS = List.of(EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.SHEEP,
            EntityType.COW,
            EntityType.MOOSHROOM,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.WOLF,
            EntityType.OCELOT,
            EntityType.RABBIT,
            EntityType.LLAMA,
            EntityType.CAT,
            EntityType.PANDA,
            EntityType.FOX,
            EntityType.BEE,
            EntityType.HOGLIN,
            EntityType.STRIDER,
            EntityType.GOAT,
            EntityType.AXOLOTL,
            EntityType.CAMEL,
            ModEntityType.ARMADILLO
    );
}
