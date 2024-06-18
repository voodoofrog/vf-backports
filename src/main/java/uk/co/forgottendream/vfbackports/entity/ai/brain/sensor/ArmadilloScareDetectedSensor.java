package uk.co.forgottendream.vfbackports.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ArmadilloScareDetectedSensor<T extends LivingEntity> extends Sensor<T> {
    private final BiPredicate<T, LivingEntity> threateningEntityPredicate;
    private final Predicate<T> canRollUpPredicate;
    private final MemoryModuleType<Boolean> memoryModuleType;
    private final int expiry;

    public ArmadilloScareDetectedSensor(int senseInterval, BiPredicate<T, LivingEntity> threateningEntityPredicate, Predicate<T> canRollUpPredicate, MemoryModuleType<Boolean> memoryModuleType, int expiry) {
        super(senseInterval);
        this.threateningEntityPredicate = threateningEntityPredicate;
        this.canRollUpPredicate = canRollUpPredicate;
        this.memoryModuleType = memoryModuleType;
        this.expiry = expiry;
    }

    protected void sense(ServerWorld world, T entity) {
        if (!this.canRollUpPredicate.test(entity)) {
            this.clear(entity);
        } else {
            this.tryDetectThreat(entity);
        }

    }

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return Set.of(MemoryModuleType.MOBS);
    }

    public void tryDetectThreat(T entity) {
        Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
        if (optional.isPresent()) {
            boolean bl = optional.get().stream().anyMatch((threat) -> this.threateningEntityPredicate.test(entity, threat));
            if (bl) {
                this.onDetected(entity);
            }

        }
    }

    public void onDetected(T entity) {
        entity.getBrain().remember(this.memoryModuleType, true, (long) this.expiry);
    }

    public void clear(T entity) {
        entity.getBrain().forget(this.memoryModuleType);
    }
}
