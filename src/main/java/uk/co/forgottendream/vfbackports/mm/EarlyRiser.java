package uk.co.forgottendream.vfbackports.mm;

import me.shedaniel.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.entity.EquipmentSlot;
import uk.co.forgottendream.vfbackports.VoodooFrogsBackports;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        VoodooFrogsBackports.LOGGER.info("{}: Running EarlyRiser...", MOD_ID);
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String equipmentSlot = remapper.mapClassName("intermediary", "net.minecraft.class_1304");
        String type = "L" + remapper.mapClassName("intermediary", "net.minecraft.class_1304$class_1305") + ";";
        ClassTinkerers.enumBuilder(equipmentSlot, type, int.class, int.class, String.class)
                .addEnum("BODY", () -> new Object[]{EquipmentSlot.Type.ARMOR, 0, 6, "body"})
                .build();
    }
}
