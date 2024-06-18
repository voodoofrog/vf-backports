package uk.co.forgottendream.vfbackports.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity;

import static net.minecraft.entity.EntityType.WOLF;
import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModEntityType {
    public static final EntityType<ArmadilloEntity> ARMADILLO;

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(MOD_ID, id), type.build());
    }

    static {
        ARMADILLO = register("armadillo", FabricEntityTypeBuilder
                .createMob()
                .entityFactory(ArmadilloEntity::new)
                .defaultAttributes(ArmadilloEntity::createArmadilloAttributes)
                .spawnGroup(SpawnGroup.CREATURE)
                .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ArmadilloEntity::canSpawn)
                .dimensions(EntityDimensions.fixed(0.7F, 0.65F)));

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.BADLANDS), ARMADILLO.getSpawnGroup(), ARMADILLO, 6, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.ERODED_BADLANDS), ARMADILLO.getSpawnGroup(), ARMADILLO, 6, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SAVANNA_PLATEAU), ARMADILLO.getSpawnGroup(), ARMADILLO, 10, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SAVANNA), ARMADILLO.getSpawnGroup(), ARMADILLO, 10, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WINDSWEPT_SAVANNA), ARMADILLO.getSpawnGroup(), ARMADILLO, 10, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WOODED_BADLANDS), ARMADILLO.getSpawnGroup(), ARMADILLO, 6, 1, 2);

        BiomeModifications.create(new Identifier(MOD_ID, "biomes"))
                .add(ModificationPhase.REMOVALS,
                        BiomeSelectors.includeByKey(BiomeKeys.GROVE),
                        context -> context.getSpawnSettings().removeSpawns((group, entry) -> group.equals(WOLF.getSpawnGroup()) && entry.type.equals(WOLF)));

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.GROVE), WOLF.getSpawnGroup(), WOLF, 1, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SAVANNA_PLATEAU), WOLF.getSpawnGroup(), WOLF, 8, 4, 8);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SPARSE_JUNGLE), WOLF.getSpawnGroup(), WOLF, 8, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WOODED_BADLANDS), WOLF.getSpawnGroup(), WOLF, 2, 4, 8);
    }
}
