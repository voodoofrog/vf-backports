package uk.co.forgottendream.vfbackports.item;

import com.google.common.collect.Multimap;
import me.shedaniel.mm.api.ClassTinkerers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import uk.co.forgottendream.vfbackports.sound.ModSoundEvents;

import java.util.function.Function;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class AnimalArmorItem extends DyeableArmorItem {
    private final Identifier entityTexture;
    @Nullable
    private final Identifier overlayTexture;
    private final Type type;

    public AnimalArmorItem(ArmorMaterial material, Type type, boolean hasOverlay, Item.Settings settings) {
        super(material, ArmorItem.Type.CHESTPLATE, settings);
        this.type = type;
        Identifier identifier = type.textureIdFunction.apply(new Identifier(MOD_ID, material.getName()));
        this.entityTexture = identifier.withSuffixedPath(".png");
        if (hasOverlay) {
            this.overlayTexture = identifier.withSuffixedPath("_overlay.png");
        } else {
            this.overlayTexture = null;
        }

    }

    public Identifier getEntityTexture() {
        return this.entityTexture;
    }

    @Nullable
    public Identifier getOverlayTexture() {
        return this.overlayTexture;
    }

    public Type getAnimalArmorType() {
        return this.type;
    }

    public SoundEvent getBreakSound() {
        return this.type.breakSound;
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return ClassTinkerers.getEnum(EquipmentSlot.class, "BODY");
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : -1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == ClassTinkerers.getEnum(EquipmentSlot.class, "BODY") ? super.getAttributeModifiers(EquipmentSlot.CHEST) : super.getAttributeModifiers(slot);
    }

    public enum Type {
        CANINE((id) -> id.withPath("textures/entity/wolf/wolf_armor"), ModSoundEvents.ITEM_WOLF_ARMOR_BREAK);

        final Function<Identifier, Identifier> textureIdFunction;
        final SoundEvent breakSound;

        Type(final Function<Identifier, Identifier> textureIdFunction, final SoundEvent breakSound) {
            this.textureIdFunction = textureIdFunction;
            this.breakSound = breakSound;
        }
    }
}
