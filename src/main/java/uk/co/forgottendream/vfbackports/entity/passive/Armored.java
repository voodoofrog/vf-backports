package uk.co.forgottendream.vfbackports.entity.passive;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public interface Armored {
    default ItemStack getBodyArmor() {
        return null;
    }

    default void setBodyArmor(ItemStack stack) {
    }

    default boolean hasArmor() {
        return false;
    }

    default void equipBodyArmor(ItemStack stack) {
    }

    default boolean shouldArmorAbsorbDamage(DamageSource source) {
        return false;
    }

    default void damageEquipment(DamageSource source, float amount, EquipmentSlot[] equipmentSlots) {
    }

    default void setBodyArmorDropChance(float chance) {
    }

    default float getBodyArmorDropChance() {
        return 0F;
    }
}
