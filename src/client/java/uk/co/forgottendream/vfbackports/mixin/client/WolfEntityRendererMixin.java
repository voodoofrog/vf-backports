package uk.co.forgottendream.vfbackports.mixin.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.forgottendream.vfbackports.VoodooFrogsBackports;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.render.entity.feature.WolfArmorFeatureRenderer;

@Mixin(WolfEntityRenderer.class)
public abstract class WolfEntityRendererMixin extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    @Unique
    private static final String BASE_PATH = "textures/entity/wolf/wolf";

    public WolfEntityRendererMixin(EntityRendererFactory.Context context, WolfEntityModel<WolfEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modInit(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new WolfArmorFeatureRenderer(this, context.getModelLoader()));
    }

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/WolfEntity;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private void getTexture(WolfEntity entity, CallbackInfoReturnable<Identifier> cir) {
        VariantHolder<WolfVariant> variantEntity = (VariantHolder<WolfVariant>) entity;
        WolfVariant variant = variantEntity.getVariant();
        String basePath = BASE_PATH + variant.texturePath();

        if (entity.hasAngerTime()) {
            basePath += "_angry";
        } else if (entity.isTamed()) {
            basePath += "_tame";
        }

        basePath += ".png";

        cir.setReturnValue(new Identifier(VoodooFrogsBackports.MOD_ID, basePath));
    }
}
