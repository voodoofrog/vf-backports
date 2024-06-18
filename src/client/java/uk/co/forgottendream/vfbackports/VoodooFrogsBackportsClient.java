package uk.co.forgottendream.vfbackports;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.item.DyeableArmorItem;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;
import uk.co.forgottendream.vfbackports.item.ModItems;
import uk.co.forgottendream.vfbackports.render.entity.ArmadilloEntityRenderer;
import uk.co.forgottendream.vfbackports.render.entity.model.ArmadilloEntityModel;
import uk.co.forgottendream.vfbackports.render.entity.model.ModEntityModelLayers;
import uk.co.forgottendream.vfbackports.render.entity.model.WolfEntityModelExtra;

public class VoodooFrogsBackportsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntityType.ARMADILLO, ArmadilloEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.ARMADILLO, ArmadilloEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WOLF_ARMOR, () -> WolfEntityModelExtra.getTexturedModelData(new Dilation(0.2F)));
        ColorProviderRegistry.ITEM.register((stack, layer) -> {
            if (stack.getItem() instanceof DyeableArmorItem armorItem && layer == 1) {
                int color = armorItem.getColor(stack);
                return color > -1 ? color : 0xC0706b;
            }
            return 0xFFFFFF;
        }, ModItems.WOLF_ARMOR);
    }
}
