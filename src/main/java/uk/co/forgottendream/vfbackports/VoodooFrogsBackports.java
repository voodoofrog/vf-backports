package uk.co.forgottendream.vfbackports;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;
import uk.co.forgottendream.vfbackports.entity.ai.brain.ModMemoryModuleType;
import uk.co.forgottendream.vfbackports.entity.ai.brain.sensor.ModSensorType;
import uk.co.forgottendream.vfbackports.entity.data.ModTrackedDataHandlerRegistry;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.item.ModItems;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;
import uk.co.forgottendream.vfbackports.sound.ModSoundEvents;
import uk.co.forgottendream.vfbackports.world.event.ModGameEvent;

public class VoodooFrogsBackports implements ModInitializer {
    public static final String MOD_ID = "vf-backports";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Reflection.initialize(
                ModSoundEvents.class,
                ModMemoryModuleType.class,
                ModEntityType.class,
                ModItems.class,
                ModSensorType.class,
                ModTrackedDataHandlerRegistry.class,
                ModGameEvent.class,
                ModRegistries.class
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> content.add(ModItems.ARMADILLO_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(ModItems.ARMADILLO_SCUTE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> content.add(ModItems.WOLF_ARMOR));
        WolfVariant.registerAndGetDefault(ModRegistries.WOLF_VARIANTS);
    }
}
