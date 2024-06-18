package uk.co.forgottendream.vfbackports.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModItems {
    public static final Item ARMADILLO_SCUTE;
    public static final Item ARMADILLO_SPAWN_EGG;
    public static final Item WOLF_ARMOR;

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, id), item);
    }

    static {
        ARMADILLO_SCUTE = register("armadillo_scute", new Item(new FabricItemSettings()));
        ARMADILLO_SPAWN_EGG = register("armadillo_spawn_egg", new SpawnEggItem(ModEntityType.ARMADILLO, 11366765, 8538184, new FabricItemSettings()));
        WOLF_ARMOR = register("wolf_armor", new AnimalArmorItem(ModArmorMaterials.ARMADILLO, AnimalArmorItem.Type.CANINE, true, new FabricItemSettings()));
    }
}
