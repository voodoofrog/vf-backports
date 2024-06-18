package uk.co.forgottendream.vfbackports.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity;
import uk.co.forgottendream.vfbackports.render.entity.model.ArmadilloEntityModel;
import uk.co.forgottendream.vfbackports.render.entity.model.ModEntityModelLayers;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ArmadilloEntityRenderer extends MobEntityRenderer<ArmadilloEntity, ArmadilloEntityModel> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/armadillo.png");

    public ArmadilloEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArmadilloEntityModel(context.getPart(ModEntityModelLayers.ARMADILLO)), 0.4F);
    }

    public Identifier getTexture(ArmadilloEntity armadilloEntity) {
        return TEXTURE;
    }
}
