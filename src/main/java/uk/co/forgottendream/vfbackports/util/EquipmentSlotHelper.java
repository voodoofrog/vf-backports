package uk.co.forgottendream.vfbackports.util;

import me.shedaniel.mm.api.ClassTinkerers;
import net.minecraft.entity.EquipmentSlot;

import java.util.Objects;

public class EquipmentSlotHelper {
    public static boolean isBodyArmor(EquipmentSlot slot) {
        return slot == ClassTinkerers.getEnum(EquipmentSlot.class, "BODY") && Objects.requireNonNull(slot.getType()) == EquipmentSlot.Type.ARMOR;
    }
}
