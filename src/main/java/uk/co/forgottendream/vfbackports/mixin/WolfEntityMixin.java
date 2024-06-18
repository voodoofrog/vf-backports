package uk.co.forgottendream.vfbackports.mixin;

import me.shedaniel.mm.api.ClassTinkerers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.forgottendream.vfbackports.datagen.ModDamageTagGenerator;
import uk.co.forgottendream.vfbackports.entity.data.ModTrackedDataHandlerRegistry;
import uk.co.forgottendream.vfbackports.entity.passive.Armored;
import uk.co.forgottendream.vfbackports.entity.passive.Cracks;
import uk.co.forgottendream.vfbackports.entity.passive.WolfVariant;
import uk.co.forgottendream.vfbackports.item.ModArmorMaterials;
import uk.co.forgottendream.vfbackports.item.ModItems;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;
import uk.co.forgottendream.vfbackports.sound.ModSoundEvents;
import uk.co.forgottendream.vfbackports.util.WolfVariantUtil;

import java.util.Objects;

import static uk.co.forgottendream.vfbackports.util.ItemStackHelper.decrementUnlessCreative;

@Mixin({WolfEntity.class})
public abstract class WolfEntityMixin extends TameableEntity implements Angerable, VariantHolder<WolfVariant>, Armored {
    @Shadow
    @Final
    private static final float TAMED_MAX_HEALTH = 40.0F;
    @Unique
    protected float bodyArmorDropChance = 0.085F;
    @Unique
    private static final TrackedData<WolfVariant> WOLF_VARIANT = DataTracker.registerData(WolfEntity.class, ModTrackedDataHandlerRegistry.WOLF_VARIANT);
    @Unique
    private static final TrackedData<ItemStack> BODY_ARMOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    protected WolfEntityMixin(EntityType<? extends WolfEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract DyeColor getCollarColor();

    @Inject(at = {@At("TAIL")}, method = {"writeCustomDataToNbt"})
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString("variant", Objects.requireNonNull(ModRegistries.WOLF_VARIANTS.getId(this.getVariant())).toString());

        NbtCompound bodyArmorNbt;
        if (!this.getBodyArmor().isEmpty()) {
            bodyArmorNbt = new NbtCompound();
            nbt.put("body_armor_item", this.getBodyArmor().writeNbt(bodyArmorNbt));
            nbt.putFloat("body_armor_drop_chance", this.bodyArmorDropChance);
        }
    }

    @Inject(at = {@At("TAIL")}, method = {"readCustomDataFromNbt"})
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("variant")) {
            WolfVariant wolfVariant = ModRegistries.WOLF_VARIANTS.get(Identifier.tryParse(nbt.getString("variant")));
            if (wolfVariant != null) {
                this.setVariant(wolfVariant);
            }
        } else {
            this.setVariant(ModRegistries.WOLF_VARIANTS.get(WolfVariant.PALE));
        }

        if (nbt.contains("body_armor_item", 10)) {
            this.setBodyArmor(ItemStack.fromNbt(nbt.getCompound("body_armor_item")));
            this.bodyArmorDropChance = nbt.getFloat("body_armor_drop_chance");
        } else {
            this.setBodyArmor(ItemStack.EMPTY);
        }
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/WolfEntity;"},
            cancellable = true
    )
    public void createChild(ServerWorld serverWorld, PassiveEntity otherParent, CallbackInfoReturnable<WolfEntity> cir) {
        WolfEntity childWolf = EntityType.WOLF.create(serverWorld);
        if (childWolf != null && otherParent instanceof WolfEntity wolfEntity) {
            VariantHolder<WolfVariant> thisWolf = this;
            VariantHolder<WolfVariant> otherParentWolf = (VariantHolder<WolfVariant>) wolfEntity;

            WolfVariant newVariant;
            if (this.random.nextBoolean()) {
                newVariant = thisWolf.getVariant();
            } else {
                newVariant = otherParentWolf.getVariant();
            }

            ((VariantHolder<WolfVariant>) childWolf).setVariant(newVariant);

            if (this.isTamed()) {
                childWolf.setOwnerUuid(this.getOwnerUuid());
                childWolf.setTamed(true);
                if (this.random.nextBoolean()) {
                    childWolf.setCollarColor(this.getCollarColor());
                } else {
                    childWolf.setCollarColor(wolfEntity.getCollarColor());
                }
            }
        }

        cir.setReturnValue(childWolf);
    }

    @Inject(at = {@At(value = "HEAD")}, method = {"interactMob"}, cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.getWorld().isClient && (!this.isBaby() || !this.isBreedingItem(itemStack))) {
            boolean bl = this.isOwner(player) || this.isTamed() || itemStack.isOf(Items.BONE) && !this.isTamed() && !this.hasAngerTime();
            cir.setReturnValue(bl ? ActionResult.CONSUME : ActionResult.PASS);
        } else if (this.isTamed()) {
            if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                decrementUnlessCreative(itemStack, 1, player);
                FoodComponent foodComponent = item.getFoodComponent();
                float f = foodComponent != null ? (float) foodComponent.getHunger() : 1.0F;
                this.heal(2.0F * f);
                cir.setReturnValue(ActionResult.success(this.getWorld().isClient()));
            } else {
                if (item instanceof DyeItem dyeItem) {
                    if (this.isOwner(player)) {
                        DyeColor dyeColor = dyeItem.getColor();
                        if (dyeColor != this.getCollarColor()) {
                            ((WolfEntity) (Object) this).setCollarColor(dyeColor);
                            decrementUnlessCreative(itemStack, 1, player);
                            cir.setReturnValue(ActionResult.SUCCESS);
                        }

                        cir.setReturnValue(super.interactMob(player, hand));
                    }
                }

                if (itemStack.isOf(ModItems.WOLF_ARMOR) && this.isOwner(player) && !this.hasArmor() && !this.isBaby()) {
                    this.equipBodyArmor(itemStack.copyWithCount(1));
                    decrementUnlessCreative(itemStack, 1, player);
                    cir.setReturnValue(ActionResult.SUCCESS);
                } else {
                    ItemStack bodyArmorStack;
                    if (!itemStack.isOf(Items.SHEARS) || !this.isOwner(player) || !this.hasArmor() || EnchantmentHelper.hasBindingCurse(this.getBodyArmor()) && !player.isCreative()) {
                        if (((ArmorMaterial) ModArmorMaterials.ARMADILLO).getRepairIngredient().test(itemStack) && this.isInSittingPose() && this.hasArmor() && this.isOwner(player) && this.getBodyArmor().isDamaged()) {
                            itemStack.decrement(1);
                            this.playSoundIfNotSilent(ModSoundEvents.ITEM_WOLF_ARMOR_REPAIR);
                            bodyArmorStack = this.getBodyArmor();
                            int i = (int) ((float) bodyArmorStack.getMaxDamage() * 0.125F);
                            bodyArmorStack.setDamage(Math.max(0, bodyArmorStack.getDamage() - i));
                            cir.setReturnValue(ActionResult.SUCCESS);
                        } else {
                            ActionResult actionResult = super.interactMob(player, hand);
                            if (!actionResult.isAccepted() && this.isOwner(player)) {
                                this.setSitting(!this.isSitting());
                                this.jumping = false;
                                this.navigation.stop();
                                this.setTarget(null);
                                cir.setReturnValue(ActionResult.SUCCESS);
                            } else {
                                cir.setReturnValue(actionResult);
                            }
                        }
                    } else {
                        itemStack.damage(1, player, (entity) -> {
                            entity.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                            if (entity instanceof PlayerEntity) {
                                entity.incrementStat(Stats.BROKEN.getOrCreateStat(item));
                            }
                        });
                        this.playSoundIfNotSilent(ModSoundEvents.ITEM_ARMOR_UNEQUIP_WOLF);
                        bodyArmorStack = this.getBodyArmor();
                        this.equipBodyArmor(ItemStack.EMPTY);
                        this.dropStack(bodyArmorStack);
                        cir.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            }
        } else if (itemStack.isOf(Items.BONE) && !this.hasAngerTime()) {
            decrementUnlessCreative(itemStack, 1, player);

            if (this.random.nextInt(3) == 0) {
                this.setOwner(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setSitting(true);
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            } else {
                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        } else if (itemStack.isOf(ModItems.WOLF_ARMOR)) {
            cir.setReturnValue(ActionResult.FAIL);
        } else {
            cir.setReturnValue(super.interactMob(player, hand));
        }
    }

    @Inject(at = {@At("TAIL")}, method = "initDataTracker")
    public void modInitDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(WOLF_VARIANT, ModRegistries.WOLF_VARIANTS.getOrThrow(WolfVariant.PALE));
        this.dataTracker.startTracking(BODY_ARMOR, ItemStack.EMPTY);
    }

    @Redirect(method = "setTamed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;setBaseValue(D)V", ordinal = 0))
    public void setTamed$setBaseValue1(EntityAttributeInstance instance, double baseValue) {
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(TAMED_MAX_HEALTH);
    }

    @Redirect(method = "setTamed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;setHealth(F)V"))
    public void setTamed$setHealth(WolfEntity instance, float v) {
        this.setHealth(TAMED_MAX_HEALTH);
    }

    @Inject(method = "getHurtSound", at = @At(value = "RETURN"), cancellable = true)
    public void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        cir.setReturnValue(this.shouldArmorAbsorbDamage(source) ? ModSoundEvents.ITEM_WOLF_ARMOR_DAMAGE : SoundEvents.ENTITY_WOLF_HURT);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.MOB_SUMMONED) {
            WolfVariant variant = ModRegistries.WOLF_VARIANTS.getRandom(this.random).orElseThrow().value();
            setVariant(variant);
        } else {
            RegistryEntry<Biome> biome = world.getBiome(this.getBlockPos());
            WolfVariant variant = WolfVariantUtil.fromBiome(biome);
            setVariant(variant);
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void applyDamage(DamageSource source, float amount) {
        if (!this.shouldArmorAbsorbDamage(source)) {
            super.applyDamage(source, amount);
        } else {
            ItemStack itemStack = this.getBodyArmor();
            int currentDamage = itemStack.getDamage();
            int maxDamage = itemStack.getMaxDamage();
            itemStack.damage(MathHelper.ceil(amount), this, (entity) -> entity.sendEquipmentBreakStatus(ClassTinkerers.getEnum(EquipmentSlot.class, "BODY")));
            if (Cracks.WOLF_ARMOR.getCrackLevel(currentDamage, maxDamage) != Cracks.WOLF_ARMOR.getCrackLevel(this.getBodyArmor())) {
                this.playSoundIfNotSilent(ModSoundEvents.ITEM_WOLF_ARMOR_CRACK);
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, ModItems.ARMADILLO_SCUTE.getDefaultStack()), this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.2, 0.1, 0.2, 0.1);
                }
            }
        }
    }

    @Override
    public WolfVariant getVariant() {
        return this.dataTracker.get(WOLF_VARIANT);
    }

    @Override
    public void setVariant(WolfVariant variant) {
        this.dataTracker.set(WOLF_VARIANT, variant);
    }

    @Override
    public ItemStack getBodyArmor() {
        return this.dataTracker.get(BODY_ARMOR);
    }

    @Override
    public boolean hasArmor() {
        return !this.getBodyArmor().isEmpty();
    }

    @Override
    public void equipBodyArmor(ItemStack stack) {
        this.equipLootStack(ClassTinkerers.getEnum(EquipmentSlot.class, "BODY"), stack);
    }

    @Override
    public void setBodyArmor(ItemStack itemStack) {
        this.dataTracker.set(BODY_ARMOR, itemStack.copy());
    }

    @Override
    public boolean shouldArmorAbsorbDamage(DamageSource source) {
        return this.hasArmor() && !source.isIn(ModDamageTagGenerator.BYPASSES_WOLF_ARMOR);
    }

    @Override
    public void damageArmor(DamageSource source, float amount) {
        this.damageEquipment(source, amount, new EquipmentSlot[]{ClassTinkerers.getEnum(EquipmentSlot.class, "BODY")});
    }

    @Override
    public void damageEquipment(DamageSource source, float amount, EquipmentSlot[] slots) {
        if (!(amount <= 0.0F)) {
            int i = (int) Math.max(1.0F, amount / 4.0F);

            for (EquipmentSlot equipmentSlot : slots) {
                ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                if (itemStack.getItem() instanceof ArmorItem && !source.isIn(DamageTypeTags.IS_FIRE)) {
                    itemStack.damage(i, this, (entity) -> entity.sendEquipmentBreakStatus(equipmentSlot));
                }
            }

        }
    }

    @Override
    public void setBodyArmorDropChance(float chance) {
        this.bodyArmorDropChance = chance;
    }

    @Override
    public float getBodyArmorDropChance() {
        return this.bodyArmorDropChance;
    }
}
