package uk.co.forgottendream.vfbackports.mixin;

import net.minecraft.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimationState.class)
public interface AnimationStateAccessor {
    @Accessor("timeRunning")
    void setTimeRunning(long value);
}
