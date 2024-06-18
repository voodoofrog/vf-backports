package uk.co.forgottendream.vfbackports.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import uk.co.forgottendream.vfbackports.entity.ModEntityStatuses;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;
import uk.co.forgottendream.vfbackports.entity.ai.brain.ModMemoryModuleType;
import uk.co.forgottendream.vfbackports.entity.ai.brain.sensor.ModSensorType;
import uk.co.forgottendream.vfbackports.entity.passive.ArmadilloEntity.State;
import uk.co.forgottendream.vfbackports.sound.ModSoundEvents;
import uk.co.forgottendream.vfbackports.util.ModTimeHelper;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("deprecation")
public class ArmadilloBrain {
    private static final float FLEE_SPEED = 2.0F;
    private static final float BREED_SPEED = 1.0F;
    private static final float TEMPT_SPEED = 1.25F;
    private static final float WALK_TO_ADULT_SPEED = 1.25F;
    private static final float STROLL_SPEED = 1.0F;
    private static final double ADULT_TEMPT_DISTANCE = 2.0;
    private static final double BABY_TEMPT_DISTANCE = 1.0;
    private static final UniformIntProvider WALK_TOWARDS_CLOSEST_ADULT_RANGE = UniformIntProvider.create(5, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super ArmadilloEntity>>> SENSOR_TYPES;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES;
    private static final SingleTickTask<ArmadilloEntity> UNROLL_TASK;

    public ArmadilloBrain() {
    }

    public static Brain.Profile<ArmadilloEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
    }

    protected static Brain<ArmadilloEntity> create(Brain<ArmadilloEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addPanicActivities(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<ArmadilloEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new UnrollAndFleeTask(FLEE_SPEED), new LookAroundTask(45, 90), new WanderAroundTask() {
            protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
                if (mobEntity instanceof ArmadilloEntity armadilloEntity) {
                    if (armadilloEntity.isNotIdle()) {
                        return false;
                    }
                }

                return super.shouldRun(serverWorld, mobEntity);
            }
        }, new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new TemptationCooldownTask(MemoryModuleType.GAZE_COOLDOWN_TICKS), UNROLL_TASK));
    }

    private static void addIdleActivities(Brain<ArmadilloEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(
                Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
                Pair.of(1, new BreedTask(ModEntityType.ARMADILLO, BREED_SPEED)),
                Pair.of(2, new RandomTask<>(ImmutableList.of(
                        Pair.of(new TemptTask((armadillo) -> TEMPT_SPEED, (armadillo) -> armadillo.isBaby() ? BABY_TEMPT_DISTANCE : ADULT_TEMPT_DISTANCE), 1),
                        Pair.of(WalkTowardClosestAdultTask.create(WALK_TOWARDS_CLOSEST_ADULT_RANGE, WALK_TO_ADULT_SPEED), 1)))
                ),
                Pair.of(3, new RandomLookAroundTask(UniformIntProvider.create(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(4, new RandomTask<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(
                        Pair.of(StrollTask.create(STROLL_SPEED), 1),
                        Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 1),
                        Pair.of(new WaitTask(30, 60), 1)))
                )
        ));
    }

    private static void addPanicActivities(Brain<ArmadilloEntity> brain) {
        brain.setTaskList(Activity.PANIC, ImmutableList.of(Pair.of(0, new RollUpTask())), Set.of(Pair.of(ModMemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryModuleState.VALUE_PRESENT), Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)));
    }

    public static void updateActivities(ArmadilloEntity armadillo) {
        armadillo.getBrain().resetPossibleActivities(ImmutableList.of(Activity.PANIC, Activity.IDLE));
    }

    static {
        SENSOR_TYPES = ImmutableList.of(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.HURT_BY,
                ModSensorType.ARMADILLO_TEMPTATIONS,
                SensorType.NEAREST_ADULT,
                ModSensorType.ARMADILLO_SCARE_DETECTED
        );
        MEMORY_MODULE_TYPES = ImmutableList.of(
                MemoryModuleType.IS_PANICKING,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.HURT_BY_ENTITY,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.PATH,
                MemoryModuleType.VISIBLE_MOBS,
                MemoryModuleType.TEMPTING_PLAYER,
                MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
                MemoryModuleType.GAZE_COOLDOWN_TICKS,
                MemoryModuleType.IS_TEMPTED,
                MemoryModuleType.BREED_TARGET,
                MemoryModuleType.NEAREST_VISIBLE_ADULT,
                ModMemoryModuleType.DANGER_DETECTED_RECENTLY
        );
        UNROLL_TASK = TaskTriggerer.task((context) -> context
                .group(context.queryMemoryAbsent(ModMemoryModuleType.DANGER_DETECTED_RECENTLY))
                .apply(context, (memoryQueryResult) -> (serverWorld, armadillo, l) -> {
                    if (armadillo.isNotIdle()) {
                        armadillo.unroll();
                        return true;
                    } else {
                        return false;
                    }
                }));
    }

    public static class UnrollAndFleeTask extends FleeTask {
        public UnrollAndFleeTask(float f) {
            super(f, ArmadilloEntity::shouldUnrollAndFlee);
        }

        protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
            armadilloEntity.unroll();
            super.run(serverWorld, armadilloEntity, l);
        }
    }

    public static class RollUpTask extends MultiTickTask<ArmadilloEntity> {
        static final int RUN_TIME_IN_TICKS;
        static final int field_49088 = 5;
        static final int field_49089 = 75;
        int ticksUntilPeek = 0;
        boolean considerPeeking;

        public RollUpTask() {
            super(Map.of(), RUN_TIME_IN_TICKS);
        }

        protected void keepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
            super.keepRunning(serverWorld, armadilloEntity, l);
            if (this.ticksUntilPeek > 0) {
                --this.ticksUntilPeek;
            }

            if (armadilloEntity.shouldSwitchToScaredState()) {
                armadilloEntity.setState(State.SCARED);
                if (armadilloEntity.isOnGround()) {
                    armadilloEntity.playSoundIfNotSilent(ModSoundEvents.ENTITY_ARMADILLO_LAND);
                }

            } else {
                ArmadilloEntity.State state = armadilloEntity.getState();
                long m = armadilloEntity.getBrain().getMemoryExpiry(ModMemoryModuleType.DANGER_DETECTED_RECENTLY);
                boolean bl = m > 75L;
                if (bl != this.considerPeeking) {
                    this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
                }

                this.considerPeeking = bl;
                if (state == State.SCARED) {
                    if (this.ticksUntilPeek == 0 && armadilloEntity.isOnGround() && bl) {
                        serverWorld.sendEntityStatus(armadilloEntity, ModEntityStatuses.PEEKING);
                        this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
                    }

                    if (m < (long) State.UNROLLING.getLengthInTicks()) {
                        armadilloEntity.playSoundIfNotSilent(ModSoundEvents.ENTITY_ARMADILLO_UNROLL_START);
                        armadilloEntity.setState(State.UNROLLING);
                    }
                } else if (state == State.UNROLLING && m > (long) State.UNROLLING.getLengthInTicks()) {
                    armadilloEntity.setState(State.SCARED);
                }
            }
        }

        private int calculateTicksUntilPeek(ArmadilloEntity entity) {
            return State.SCARED.getLengthInTicks() + entity.getRandom().nextBetween(100, 400);
        }

        protected boolean shouldRun(ServerWorld serverWorld, ArmadilloEntity armadilloEntity) {
            return armadilloEntity.isOnGround();
        }

        protected boolean shouldKeepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
            return armadilloEntity.getState().shouldRunRollUpTask();
        }

        protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
            armadilloEntity.startRolling();
        }

        protected void finishRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
            if (!armadilloEntity.canRollUp()) {
                armadilloEntity.unroll();
            }
        }

        static {
            RUN_TIME_IN_TICKS = 5 * ModTimeHelper.MINUTE_IN_SECONDS * 20;
        }
    }
}
