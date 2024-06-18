package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModDamageTagGenerator extends FabricTagProvider<DamageType> {
    public static final TagKey<DamageType> BYPASSES_WOLF_ARMOR = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MOD_ID, "bypasses_wolf_armor"));

    public ModDamageTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BYPASSES_WOLF_ARMOR)
                .addOptionalTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(DamageTypes.CRAMMING)
                .add(DamageTypes.DROWN)
                .add(DamageTypes.DRY_OUT)
                .add(DamageTypes.FREEZE)
                .add(DamageTypes.IN_WALL)
                .add(DamageTypes.INDIRECT_MAGIC)
                .add(DamageTypes.MAGIC)
                .add(DamageTypes.OUTSIDE_BORDER)
                .add(DamageTypes.STARVE)
                .add(DamageTypes.THORNS)
                .add(DamageTypes.WITHER);
    }
}
