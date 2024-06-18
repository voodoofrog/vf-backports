package uk.co.forgottendream.vfbackports.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackHelper {
    public static void decrementUnlessCreative(ItemStack itemStack, int amount, @Nullable PlayerEntity entity) {
        if (entity == null || !entity.isCreative()) {
            itemStack.decrement(amount);
        }
    }
}
