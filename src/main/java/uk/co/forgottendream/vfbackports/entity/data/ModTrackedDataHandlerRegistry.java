package uk.co.forgottendream.vfbackports.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;

public class ModTrackedDataHandlerRegistry {
    public static final TrackedDataHandler<ArmadilloEntity.State> ARMADILLO_STATE;
    public static final TrackedDataHandler<WolfVariant> WOLF_VARIANT;

    public static void register(TrackedDataHandler<?> handler) {
        TrackedDataHandlerRegistry.register(handler);
    }

    private ModTrackedDataHandlerRegistry() {
    }

    static {
        ARMADILLO_STATE = TrackedDataHandler.ofEnum(ArmadilloEntity.State.class);
        WOLF_VARIANT = TrackedDataHandler.of(ModRegistries.WOLF_VARIANTS);
        register(ARMADILLO_STATE);
        register(WOLF_VARIANT);
    }
}
