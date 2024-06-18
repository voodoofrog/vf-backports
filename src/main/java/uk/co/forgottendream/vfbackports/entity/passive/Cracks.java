package uk.co.forgottendream.vfbackports.entity.passive;

import net.minecraft.item.ItemStack;

public class Cracks {
    public static final Cracks WOLF_ARMOR = new Cracks(0.95F, 0.69F, 0.32F);
    private final float lowCrackThreshold;
    private final float mediumCrackThreshold;
    private final float highCrackThreshold;

    private Cracks(float lowCrackThreshold, float mediumCrackThreshold, float highCrackThreshold) {
        this.lowCrackThreshold = lowCrackThreshold;
        this.mediumCrackThreshold = mediumCrackThreshold;
        this.highCrackThreshold = highCrackThreshold;
    }

    public CrackLevel getCrackLevel(float health) {
        if (health < this.highCrackThreshold) {
            return Cracks.CrackLevel.HIGH;
        } else if (health < this.mediumCrackThreshold) {
            return Cracks.CrackLevel.MEDIUM;
        } else {
            return health < this.lowCrackThreshold ? Cracks.CrackLevel.LOW : Cracks.CrackLevel.NONE;
        }
    }

    public CrackLevel getCrackLevel(ItemStack stack) {
        return !stack.isDamageable() ? Cracks.CrackLevel.NONE : this.getCrackLevel(stack.getDamage(), stack.getMaxDamage());
    }

    public CrackLevel getCrackLevel(int currentDamage, int maxDamage) {
        return this.getCrackLevel((float) (maxDamage - currentDamage) / (float) maxDamage);
    }

    public enum CrackLevel {
        NONE,
        LOW,
        MEDIUM,
        HIGH;

        CrackLevel() {
        }
    }
}
