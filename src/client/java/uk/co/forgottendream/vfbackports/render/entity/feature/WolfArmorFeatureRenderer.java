package uk.co.forgottendream.vfbackports.render.entity.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import uk.co.forgottendream.vfbackports.entity.passive.Cracks;
import uk.co.forgottendream.vfbackports.entity.passive.Cracks.CrackLevel;
import uk.co.forgottendream.vfbackports.item.AnimalArmorItem;
import uk.co.forgottendream.vfbackports.render.entity.model.ModEntityModelLayers;

import java.util.Map;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class WolfArmorFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    private final WolfEntityModel<WolfEntity> model;
    private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES;

    public WolfArmorFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context, EntityModelLoader loader) {
        super(context);
        this.model = new WolfEntityModel<>(loader.getModelPart(ModEntityModelLayers.WOLF_ARMOR));
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntity wolfEntity, float f, float g, float h, float j, float k, float l) {
        if (wolfEntity.hasArmor()) {
            ItemStack itemStack = wolfEntity.getBodyArmor();
            if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem) {
                if (animalArmorItem.getAnimalArmorType() == AnimalArmorItem.Type.CANINE) {
                    this.getContextModel().copyStateTo(this.model);
                    this.model.animateModel(wolfEntity, f, g, h);
                    this.model.setAngles(wolfEntity, f, g, j, k, l);
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
                    this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    this.renderDyed(matrixStack, vertexConsumerProvider, i, itemStack, animalArmorItem);
                    this.renderCracks(matrixStack, vertexConsumerProvider, i, itemStack);
                }
            }

        }
    }

    private void renderDyed(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, AnimalArmorItem item) {
        if (stack.getItem() instanceof DyeableArmorItem) {
            int i = item.getColor(stack);

            if (i == -1) {
                return;
            }

            Identifier identifier = item.getOverlayTexture();
            if (identifier == null) {
                return;
            }

            float f = (float) ColorHelper.Argb.getRed(i) / 255.0F;
            float g = (float) ColorHelper.Argb.getGreen(i) / 255.0F;
            float h = (float) ColorHelper.Argb.getBlue(i) / 255.0F;
            this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier)), light, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
        }

    }

    private void renderCracks(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {
        CrackLevel crackLevel = Cracks.WOLF_ARMOR.getCrackLevel(stack);
        if (crackLevel != CrackLevel.NONE) {
            Identifier identifier = CRACK_TEXTURES.get(crackLevel);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(identifier));
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    static {
        CRACK_TEXTURES = Map.of(
                CrackLevel.LOW, new Identifier(MOD_ID, "textures/entity/wolf/wolf_armor_crackiness_low.png"),
                CrackLevel.MEDIUM, new Identifier(MOD_ID, "textures/entity/wolf/wolf_armor_crackiness_medium.png"),
                CrackLevel.HIGH, new Identifier(MOD_ID, "textures/entity/wolf/wolf_armor_crackiness_high.png")
        );
    }
}
