package uk.co.forgottendream.vfbackports.entity.passive;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.registry.ModRegistryKeys;

public record WolfVariant(String texturePath) {
    public static final RegistryKey<WolfVariant> PALE = of("pale");
    public static final RegistryKey<WolfVariant> SPOTTED = of("spotted");
    public static final RegistryKey<WolfVariant> SNOWY = of("snowy");
    public static final RegistryKey<WolfVariant> BLACK = of("black");
    public static final RegistryKey<WolfVariant> ASHEN = of("ashen");
    public static final RegistryKey<WolfVariant> RUSTY = of("rusty");
    public static final RegistryKey<WolfVariant> WOODS = of("woods");
    public static final RegistryKey<WolfVariant> CHESTNUT = of("chestnut");
    public static final RegistryKey<WolfVariant> STRIPED = of("striped");

    private static RegistryKey<WolfVariant> of(String id) {
        return RegistryKey.of(ModRegistryKeys.WOLF_VARIANT, new Identifier(id));
    }

    public static WolfVariant registerAndGetDefault(Registry<WolfVariant> registry) {
        register(registry, SPOTTED, "_spotted");
        register(registry, SNOWY, "_snowy");
        register(registry, BLACK, "_black");
        register(registry, ASHEN, "_ashen");
        register(registry, RUSTY, "_rusty");
        register(registry, WOODS, "_woods");
        register(registry, CHESTNUT, "_chestnut");
        register(registry, STRIPED, "_striped");
        return register(registry, PALE, "");
    }

    private static WolfVariant register(Registry<WolfVariant> registry, RegistryKey<WolfVariant> key, String texturePath) {
        return Registry.register(registry, key, new WolfVariant(texturePath));
    }

    public String texturePath() {
        return this.texturePath;
    }
}
