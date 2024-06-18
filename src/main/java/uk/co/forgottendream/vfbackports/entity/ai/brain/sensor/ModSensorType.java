package uk.co.forgottendream.vfbackports.entity.ai.brain.sensor;

import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.datagen.ModItemTagGenerator;
import uk.co.forgottendream.vfbackports.entity.ai.brain.ModMemoryModuleType;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity;

import java.util.function.Supplier;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;
import static uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity.SCARE_EXPIRY;

public class ModSensorType {
    public static final SensorType<TemptationsSensor> ARMADILLO_TEMPTATIONS = register("armadillo_temptations", () ->
            new TemptationsSensor(Ingredient.fromTag(ModItemTagGenerator.ARMADILLO_FOOD)));
    public static final SensorType<ArmadilloScareDetectedSensor<ArmadilloEntity>> ARMADILLO_SCARE_DETECTED = register("armadillo_scare_detected", () ->
            new ArmadilloScareDetectedSensor<>(5, ArmadilloEntity::isEntityThreatening, ArmadilloEntity::canRollUp, ModMemoryModuleType.DANGER_DETECTED_RECENTLY, SCARE_EXPIRY));

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(Registries.SENSOR_TYPE, new Identifier(MOD_ID, id), new SensorType<>(factory));
    }
}
