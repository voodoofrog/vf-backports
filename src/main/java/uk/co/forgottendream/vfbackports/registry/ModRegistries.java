package uk.co.forgottendream.vfbackports.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;

public class ModRegistries {
    public static final Registry<WolfVariant> WOLF_VARIANTS;

    static {
        WOLF_VARIANTS = FabricRegistryBuilder
                .createSimple(RegistryKey.<WolfVariant>ofRegistry(new Identifier("wolf_variant")))
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }
}
