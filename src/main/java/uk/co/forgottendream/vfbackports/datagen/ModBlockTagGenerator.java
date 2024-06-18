package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public static final TagKey<Block> ARMADILLO_SPAWNABLE_ON = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "armadillo_spawnable_on"));

    public ModBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ARMADILLO_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .add(Blocks.TERRACOTTA)
                .add(Blocks.WHITE_TERRACOTTA)
                .add(Blocks.YELLOW_TERRACOTTA)
                .add(Blocks.ORANGE_TERRACOTTA)
                .add(Blocks.RED_TERRACOTTA)
                .add(Blocks.BROWN_TERRACOTTA)
                .add(Blocks.LIGHT_GRAY_TERRACOTTA)
                .add(Blocks.RED_SAND)
                .add(Blocks.COARSE_DIRT);

        getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("wolves_spawnable_on")))
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.PODZOL);
    }
}
