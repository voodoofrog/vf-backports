package uk.co.forgottendream.vfbackports.entity.passive;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import uk.co.forgottendream.vfbackports.datagen.ModBlockTagGenerator;
import uk.co.forgottendream.vfbackports.datagen.ModItemTagGenerator;
import uk.co.forgottendream.vfbackports.entity.ModEntityStatuses;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;
import uk.co.forgottendream.vfbackports.entity.ai.brain.ModMemoryModuleType;
import uk.co.forgottendream.vfbackports.entity.data.ModTrackedDataHandlerRegistry;
import uk.co.forgottendream.vfbackports.item.ModItems;
import uk.co.forgottendream.vfbackports.mixin.AnimationStateAccessor;
import uk.co.forgottendream.vfbackports.sound.ModSoundEvents;
import uk.co.forgottendream.vfbackports.util.ModTimeHelper;
import uk.co.forgottendream.vfbackports.world.event.ModGameEvent;

import java.util.Iterator;

import static net.minecraft.entity.EntityGroup.UNDEAD;

@SuppressWarnings("deprecation")
public class ArmadilloEntity extends AnimalEntity {
    public static final float BABY_SCALE_FACTOR = 0.6F;
    public static final float HEAD_YAW = 32.5F;
    public static final int SCARE_EXPIRY = 80;
    private static final double WIDTH = 7.0;
    private static final double HEIGHT = 2.0;
    private static final TrackedData<State> STATE;
    private long currentStateTicks = 0L;
    public final AnimationState unrollingAnimationState = new AnimationState();
    public final AnimationState rollingAnimationState = new AnimationState();
    public final AnimationState scaredAnimationState = new AnimationState();
    private int nextScuteShedCooldown;
    private boolean peeking = false;

    public ArmadilloEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
        this.nextScuteShedCooldown = this.getNextScuteShedCooldown();
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntityType.ARMADILLO.create(world);
    }

    public static DefaultAttributeContainer.Builder createArmadilloAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.14);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STATE, State.IDLE);
    }

    public boolean isNotIdle() {
        return this.dataTracker.get(STATE) != State.IDLE;
    }

    public boolean isRolledUp() {
        return this.getState().isRolledUp(this.currentStateTicks);
    }

    public boolean shouldSwitchToScaredState() {
        return this.getState() == State.ROLLING && this.currentStateTicks > (long) State.ROLLING.getLengthInTicks();
    }

    public State getState() {
        return this.dataTracker.get(STATE);
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public void setState(State state) {
        this.dataTracker.set(STATE, state);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STATE.equals(data)) {
            this.currentStateTicks = 0L;
        }

        super.onTrackedDataSet(data);
    }

    @Override
    protected Brain.Profile<ArmadilloEntity> createBrainProfile() {
        return ArmadilloBrain.createBrainProfile();
    }

    @Override
    protected Brain<ArmadilloEntity> deserializeBrain(Dynamic<?> dynamic) {
        return ArmadilloBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    protected void mobTick() {
        @SuppressWarnings("unchecked")
        Brain<ArmadilloEntity> brain = (Brain<ArmadilloEntity>) this.getBrain();

        this.getWorld().getProfiler().push("armadilloBrain");
        brain.tick((ServerWorld) this.getWorld(), this);
        this.getWorld().getProfiler().pop();
        this.getWorld().getProfiler().push("armadilloActivityUpdate");
        ArmadilloBrain.updateActivities(this);
        this.getWorld().getProfiler().pop();
        if (this.isAlive() && !this.isBaby() && --this.nextScuteShedCooldown <= 0) {
            this.playSound(ModSoundEvents.ENTITY_ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(ModItems.ARMADILLO_SCUTE);
            this.emitGameEvent(GameEvent.ENTITY_PLACE);
            this.nextScuteShedCooldown = this.getNextScuteShedCooldown();
        }

        super.mobTick();
    }

    private int getNextScuteShedCooldown() {
        return this.random.nextInt(20 * ModTimeHelper.MINUTE_IN_SECONDS * 5) + 20 * ModTimeHelper.MINUTE_IN_SECONDS * 5;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            this.updateAnimationStates();
        }

        if (this.isNotIdle()) {
            this.clampHeadYaw();
        }

        ++this.currentStateTicks;
    }

    @Override
    public float getScaleFactor() {
        return this.isBaby() ? BABY_SCALE_FACTOR : 1.0F;
    }

    private void updateAnimationStates() {
        switch (this.getState().ordinal()) {
            case 0:
                this.unrollingAnimationState.stop();
                this.rollingAnimationState.stop();
                this.scaredAnimationState.stop();
                break;
            case 1:
                this.unrollingAnimationState.stop();
                this.rollingAnimationState.startIfNotRunning(this.age);
                this.scaredAnimationState.stop();
                break;
            case 2:
                this.unrollingAnimationState.stop();
                this.rollingAnimationState.stop();
                if (this.peeking) {
                    this.scaredAnimationState.stop();
                    this.peeking = false;
                }

                if (this.currentStateTicks == 0L) {
                    this.scaredAnimationState.start(this.age);
                    if (this.scaredAnimationState.isRunning()) {
                        long skip = this.scaredAnimationState.getTimeRunning() + (long) ((float) (State.SCARED.getLengthInTicks() * 1000)) / 20L;
                        ((AnimationStateAccessor) this.scaredAnimationState).setTimeRunning(skip);
                    }
                } else {
                    this.scaredAnimationState.startIfNotRunning(this.age);
                }
                break;
            case 3:
                this.unrollingAnimationState.startIfNotRunning(this.age);
                this.rollingAnimationState.stop();
                this.scaredAnimationState.stop();
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == ModEntityStatuses.PEEKING && this.getWorld().isClient) {
            this.peeking = true;
            this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), ModSoundEvents.ENTITY_ARMADILLO_PEEK, this.getSoundCategory(), 1.0F, 1.0F, false);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModItemTagGenerator.ARMADILLO_FOOD);
    }

    public static boolean canSpawn(EntityType<ArmadilloEntity> entityType, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getBlockState(pos.down()).isIn(ModBlockTagGenerator.ARMADILLO_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
    }

    public boolean isEntityThreatening(LivingEntity entity) {
        if (!this.getBoundingBox().expand(WIDTH, HEIGHT, WIDTH).intersects(entity.getBoundingBox())) {
            return false;
        } else if (entity.getGroup() == UNDEAD) {
            return true;
        } else if (this.getAttacker() == entity) {
            return true;
        } else if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isSpectator()) {
                return false;
            } else {
                return playerEntity.isSprinting() || playerEntity.hasVehicle();
            }
        } else {
            return false;
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("state", this.getState().asString());
        nbt.putInt("scute_time", this.nextScuteShedCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setState(State.fromName(nbt.getString("state")));
        if (nbt.contains("scute_time")) {
            this.nextScuteShedCooldown = nbt.getInt("scute_time");
        }

    }

    public void startRolling() {
        if (!this.isNotIdle()) {
            this.stopMovement();
            this.resetLoveTicks();
            this.emitGameEvent(ModGameEvent.ENTITY_ACTION);
            this.playSound(ModSoundEvents.ENTITY_ARMADILLO_ROLL, this.getSoundVolume(), this.getSoundPitch());
            this.setState(State.ROLLING);
        }
    }

    public void unroll() {
        if (this.isNotIdle()) {
            this.emitGameEvent(ModGameEvent.ENTITY_ACTION);
            this.playSound(ModSoundEvents.ENTITY_ARMADILLO_UNROLL_FINISH, this.getSoundVolume(), this.getSoundPitch());
            this.setState(State.IDLE);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isNotIdle()) {
            amount = (amount - 1.0F) / 2.0F;
        }

        return super.damage(source, amount);
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        super.applyDamage(source, amount);
        if (!this.isAiDisabled() && !this.isDead()) {
            if (source.getAttacker() instanceof LivingEntity) {
                this.getBrain().remember(ModMemoryModuleType.DANGER_DETECTED_RECENTLY, true, SCARE_EXPIRY);
                if (this.canRollUp()) {
                    this.startRolling();
                }
            } else if (shouldUnrollAndFlee(this)) {
                this.unroll();
            }

        }
    }

    public static boolean shouldUnrollAndFlee(PathAwareEntity entity) {
        return entity.isOnFire() || entity.shouldEscapePowderSnow();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.BRUSH) && this.brushScute()) {
            itemStack.damage(16, player, (playerEntity) -> {
                EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                playerEntity.sendEquipmentBreakStatus(slot);
            });
            return ActionResult.success(this.getWorld().isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    public void growUp(int age, boolean overGrow) {
        if (this.isBaby() && overGrow) {
            this.playSound(ModSoundEvents.ENTITY_ARMADILLO_EAT, this.getSoundVolume(), this.getSoundPitch());
        }

        super.growUp(age, overGrow);
    }

    public boolean brushScute() {
        if (this.isBaby()) {
            return false;
        } else {
            this.dropStack(new ItemStack(ModItems.ARMADILLO_SCUTE));
            this.emitGameEvent(GameEvent.ENTITY_INTERACT);
            this.playSoundIfNotSilent(ModSoundEvents.ENTITY_ARMADILLO_BRUSH);
            return true;
        }
    }

    public boolean canRollUp() {
        return !this.isPanicking() && !this.isInFluid() && !this.isLeashed() && !this.hasVehicle() && !this.hasPassengers();
    }

    @Override
    public void lovePlayer(@Nullable PlayerEntity player) {
        super.lovePlayer(player);
        this.playSound(ModSoundEvents.ENTITY_ARMADILLO_EAT, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public boolean canEat() {
        return super.canEat() && !this.isNotIdle();
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return ModSoundEvents.ENTITY_ARMADILLO_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isNotIdle() ? null : ModSoundEvents.ENTITY_ARMADILLO_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_ARMADILLO_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isNotIdle() ? ModSoundEvents.ENTITY_ARMADILLO_HURT_REDUCED : ModSoundEvents.ENTITY_ARMADILLO_HURT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSoundEvents.ENTITY_ARMADILLO_STEP, 0.15F, 1.0F);
    }

    @Override
    public int getMaxHeadRotation() {
        return this.isNotIdle() ? 0 : 32;
    }

    @Override
    protected BodyControl createBodyControl() {
        return new BodyControl(this) {
            public void tick() {
                if (!ArmadilloEntity.this.isNotIdle()) {
                    super.tick();
                }

            }
        };
    }

    protected void clampHeadYaw() {
        float f = (float) this.getMaxHeadRotation();
        float g = this.getHeadYaw();
        float h = MathHelper.wrapDegrees(this.bodyYaw - g);
        float i = MathHelper.clamp(MathHelper.wrapDegrees(this.bodyYaw - g), -f, f);
        float j = g + h - i;
        this.setHeadYaw(j);
    }

    public void stopMovement() {
        this.getNavigation().stop();
        this.setSidewaysSpeed(0.0F);
        this.setUpwardSpeed(0.0F);
        this.setMovementSpeed(0.0F);
    }

    public boolean isPanicking() {
        if (this.brain.hasMemoryModule(MemoryModuleType.IS_PANICKING)) {
            return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent();
        } else {
            Iterator<PrioritizedGoal> var1 = this.goalSelector.getGoals().iterator();

            PrioritizedGoal prioritizedGoal;
            do {
                if (!var1.hasNext()) {
                    return false;
                }

                prioritizedGoal = var1.next();
            } while (!prioritizedGoal.isRunning() || !(prioritizedGoal.getGoal() instanceof EscapeDangerGoal));

            return true;
        }
    }

    public boolean isInFluid() {
        return this.isInsideWaterOrBubbleColumn() || this.isInLava();
    }

    static {
        STATE = DataTracker.registerData(ArmadilloEntity.class, ModTrackedDataHandlerRegistry.ARMADILLO_STATE);
    }

    public enum State implements StringIdentifiable {
        IDLE("idle", false, 0) {
            public boolean isRolledUp(long currentStateTicks) {
                return false;
            }
        },
        ROLLING("rolling", true, 10) {
            public boolean isRolledUp(long currentStateTicks) {
                return currentStateTicks > 5L;
            }
        },
        SCARED("scared", true, 50) {
            public boolean isRolledUp(long currentStateTicks) {
                return true;
            }
        },
        UNROLLING("unrolling", true, 30) {
            public boolean isRolledUp(long currentStateTicks) {
                return currentStateTicks < 26L;
            }
        };

        private static final StringIdentifiable.Codec<State> CODEC = StringIdentifiable.createCodec(State::values);

        private final String name;
        private final boolean runRollUpTask;
        private final int lengthInTicks;

        State(final String name, final boolean runRollUpTask, final int lengthInTicks) {
            this.name = name;
            this.runRollUpTask = runRollUpTask;
            this.lengthInTicks = lengthInTicks;
        }

        public static State fromName(String name) {
            return CODEC.byId(name, IDLE);
        }

        @Override
        public String asString() {
            return this.name;
        }

        public abstract boolean isRolledUp(long currentStateTicks);

        public boolean shouldRunRollUpTask() {
            return this.runRollUpTask;
        }

        public int getLengthInTicks() {
            return this.lengthInTicks;
        }
    }
}
