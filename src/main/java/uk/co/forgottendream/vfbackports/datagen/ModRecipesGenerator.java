package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import uk.co.forgottendream.vfbackports.item.ModItems;

import java.util.function.Consumer;

public class ModRecipesGenerator extends FabricRecipeProvider {
    public ModRecipesGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.WOLF_ARMOR)
                .pattern("X  ")
                .pattern("XXX")
                .pattern("X X")
                .input('X', ModItems.ARMADILLO_SCUTE)
                .criterion(FabricRecipeProvider.hasItem(ModItems.ARMADILLO_SCUTE),
                        FabricRecipeProvider.conditionsFromItem(ModItems.ARMADILLO_SCUTE))
                .offerTo(exporter);
    }
}
