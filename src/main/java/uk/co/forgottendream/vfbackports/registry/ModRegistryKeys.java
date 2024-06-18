package uk.co.forgottendream.vfbackports.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;

public class ModRegistryKeys {
    public static final RegistryKey<Registry<WolfVariant>> WOLF_VARIANT = of("wolf_variant");

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(new Identifier(id));
    }
}
