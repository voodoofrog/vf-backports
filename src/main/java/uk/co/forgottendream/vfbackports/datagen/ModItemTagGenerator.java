package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> ARMADILLO_FOOD = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "armadillo_food"));
    public static final TagKey<Item> MEAT = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "meat"));
    public static final TagKey<Item> WOLF_FOOD = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "wolf_food"));

    public ModItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ARMADILLO_FOOD)
                .add(Items.SPIDER_EYE);

        getOrCreateTagBuilder(MEAT)
                .add(Items.BEEF)
                .add(Items.CHICKEN)
                .add(Items.COOKED_BEEF)
                .add(Items.COOKED_CHICKEN)
                .add(Items.COOKED_MUTTON)
                .add(Items.COOKED_PORKCHOP)
                .add(Items.COOKED_RABBIT)
                .add(Items.MUTTON)
                .add(Items.PORKCHOP)
                .add(Items.RABBIT)
                .add(Items.ROTTEN_FLESH);

        getOrCreateTagBuilder(WOLF_FOOD)
                .addOptionalTag(MEAT);
    }
}
