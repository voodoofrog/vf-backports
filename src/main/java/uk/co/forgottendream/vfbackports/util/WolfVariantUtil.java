package uk.co.forgottendream.vfbackports.util;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;

public class WolfVariantUtil {
    public static WolfVariant fromBiome(RegistryEntry<Biome> biome) {
        if (biome.isIn(BiomeTags.IS_SAVANNA)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.SPOTTED);
        if (biome.matchesKey(BiomeKeys.GROVE)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.SNOWY);
        if (biome.matchesKey(BiomeKeys.OLD_GROWTH_PINE_TAIGA)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.BLACK);
        if (biome.matchesKey(BiomeKeys.SNOWY_TAIGA)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.ASHEN);
        if (biome.isIn(BiomeTags.IS_JUNGLE)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.RUSTY);
        if (biome.matchesKey(BiomeKeys.FOREST)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.WOODS);
        if (biome.matchesKey(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.CHESTNUT);
        if (biome.isIn(BiomeTags.IS_BADLANDS)) return ModRegistries.WOLF_VARIANTS.get(WolfVariant.STRIPED);
        return ModRegistries.WOLF_VARIANTS.get(WolfVariant.PALE);
    }
}
