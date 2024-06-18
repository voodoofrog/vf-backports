package uk.co.forgottendream.vfbackports.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.forgottendream.vfbackports.util.EquipmentSlotHelper;

@Mixin({MobEntity.class})
public abstract class MobEntityMixin extends LivingEntity implements Targeter {
    @Final
    @Shadow
    private DefaultedList<ItemStack> handItems;

    @Final
    @Shadow
    private DefaultedList<ItemStack> armorItems;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V", at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;processEquippedStack(Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER)}, cancellable = true)
    public void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            MobEntity realThis = ((MobEntity) (Object) this);
            if (realThis instanceof WolfEntity wolfEntity) {
                ItemStack itemStack = wolfEntity.getBodyArmor();
                wolfEntity.setBodyArmor(stack);
                this.onEquipStack(slot, itemStack, stack);
                ci.cancel();
            }
        }
    }

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    public void getEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            MobEntity realThis = ((MobEntity) (Object) this);
            if (realThis instanceof WolfEntity wolfEntity) {
                cir.setReturnValue(wolfEntity.getBodyArmor());
            }
        }
    }

    @Inject(method = "updateDropChances", at = @At("HEAD"))
    public void updateDropChances(EquipmentSlot slot, CallbackInfo ci) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            MobEntity realThis = ((MobEntity) (Object) this);
            if (realThis instanceof WolfEntity wolfEntity) {
                wolfEntity.setBodyArmorDropChance(2.0F);
            }
        }
    }

    @Inject(method = "setEquipmentDropChance", at = @At("HEAD"))
    public void setEquipmentDropChance(EquipmentSlot slot, float chance, CallbackInfo ci) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            MobEntity realThis = ((MobEntity) (Object) this);
            if (realThis instanceof WolfEntity wolfEntity) {
                wolfEntity.setBodyArmorDropChance(chance);
            }
        }
    }

    @Inject(method = "getDropChance", at = @At("HEAD"), cancellable = true)
    protected void getDropChance(EquipmentSlot slot, CallbackInfoReturnable<Float> cir) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            MobEntity realThis = ((MobEntity) (Object) this);
            if (realThis instanceof WolfEntity wolfEntity) {
                cir.setReturnValue(wolfEntity.getBodyArmorDropChance());
            }
        }
    }
}
