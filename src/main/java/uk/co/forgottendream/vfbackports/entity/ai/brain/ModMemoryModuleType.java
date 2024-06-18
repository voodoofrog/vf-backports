package uk.co.forgottendream.vfbackports.entity.ai.brain;

import com.mojang.serialization.Codec;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModMemoryModuleType {
    public static final MemoryModuleType<Boolean> DANGER_DETECTED_RECENTLY;

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, new Identifier(MOD_ID, id), new MemoryModuleType<>(Optional.of(codec)));
    }

    static {
        DANGER_DETECTED_RECENTLY = register("danger_detected_recently", Codec.BOOL);
    }
}
