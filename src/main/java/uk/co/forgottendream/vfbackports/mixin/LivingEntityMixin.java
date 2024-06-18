package uk.co.forgottendream.vfbackports.mixin;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import me.shedaniel.mm.api.ClassTinkerers;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.forgottendream.vfbackports.entity.ModEntityStatuses;
import uk.co.forgottendream.vfbackports.item.AnimalArmorItem;
import uk.co.forgottendream.vfbackports.util.EquipmentSlotHelper;

import java.util.List;
import java.util.Map;

@Debug(export = true)
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements Attackable {
    @Unique
    private ItemStack syncedBodyArmorStack = ItemStack.EMPTY;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "handleStatus", cancellable = true)
    public void handleStatus(byte status, CallbackInfo ci) {
        LivingEntity realThis = ((LivingEntity) (Object) this);
        if (status == ModEntityStatuses.BREAK_BODY) {
            realThis.playEquipmentBreakEffects(realThis.getEquippedStack(ClassTinkerers.getEnum(EquipmentSlot.class, "BODY")));
            ci.cancel();
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getEquipmentBreakStatus"}, cancellable = true)
    private static void getEquipmentBreakStatus(EquipmentSlot slot, CallbackInfoReturnable<Byte> cir) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            cir.setReturnValue(ModEntityStatuses.BREAK_BODY);
        }
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V")}, method = "playEquipmentBreakEffects")
    public final void playEquipmentBreakEffects$playSound(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original, ItemStack stack) {
        if (stack.getItem() instanceof AnimalArmorItem animalArmorItem) {
            instance.playSound(x, y, z, animalArmorItem.getBreakSound(), category, volume, pitch, useDistance);
        } else {
            original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
        }
    }

    @Inject(method = {"getEquipmentChanges()Ljava/util/Map;"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EquipmentSlot;getType()Lnet/minecraft/entity/EquipmentSlot$Type;"), cancellable = true)
    private void getEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, @Local EquipmentSlot slot, @Local LocalRef<Map<EquipmentSlot, ItemStack>> mapRef) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            LivingEntity realThis = ((LivingEntity) (Object) this);
            ItemStack oldStack = this.syncedBodyArmorStack;
            ItemStack currentStack = realThis.getEquippedStack(slot);

            if (realThis.areItemsDifferent(oldStack, currentStack)) {
                if (mapRef.get() == null) {
                    mapRef.set(Maps.newEnumMap(EquipmentSlot.class));
                }

                mapRef.get().put(slot, currentStack);
                if (!oldStack.isEmpty()) {
                    realThis.getAttributes().removeModifiers(oldStack.getAttributeModifiers(slot));
                }

                if (!currentStack.isEmpty()) {
                    realThis.getAttributes().addTemporaryModifiers(currentStack.getAttributeModifiers(slot));
                }
            }

            cir.setReturnValue(mapRef.get());
        }
    }

    @Inject(method = "method_30120", at = {@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER)})
    public void sendEquipmentChanges$forEach(List<Pair<EquipmentSlot, ItemStack>> list, EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (EquipmentSlotHelper.isBodyArmor(slot)) {
            this.syncedBodyArmorStack = stack.copy();
        }
    }
}
