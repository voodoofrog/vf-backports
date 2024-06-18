package uk.co.forgottendream.vfbackports.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static uk.co.forgottendream.vfbackports.VoodooFrogsBackports.MOD_ID;

public class ModSoundEvents {
    public static final SoundEvent ENTITY_ARMADILLO_EAT = register("entity.armadillo.eat");
    public static final SoundEvent ENTITY_ARMADILLO_HURT = register("entity.armadillo.hurt");
    public static final SoundEvent ENTITY_ARMADILLO_HURT_REDUCED = register("entity.armadillo.hurt_reduced");
    public static final SoundEvent ENTITY_ARMADILLO_AMBIENT = register("entity.armadillo.ambient");
    public static final SoundEvent ENTITY_ARMADILLO_STEP = register("entity.armadillo.step");
    public static final SoundEvent ENTITY_ARMADILLO_DEATH = register("entity.armadillo.death");
    public static final SoundEvent ENTITY_ARMADILLO_ROLL = register("entity.armadillo.roll");
    public static final SoundEvent ENTITY_ARMADILLO_LAND = register("entity.armadillo.land");
    public static final SoundEvent ENTITY_ARMADILLO_SCUTE_DROP = register("entity.armadillo.scute_drop");
    public static final SoundEvent ENTITY_ARMADILLO_UNROLL_FINISH = register("entity.armadillo.unroll_finish");
    public static final SoundEvent ENTITY_ARMADILLO_PEEK = register("entity.armadillo.peek");
    public static final SoundEvent ENTITY_ARMADILLO_UNROLL_START = register("entity.armadillo.unroll_start");
    public static final SoundEvent ENTITY_ARMADILLO_BRUSH = register("entity.armadillo.brush");
    public static final SoundEvent ITEM_ARMOR_EQUIP_WOLF = register("item.armor.equip_wolf");
    public static final SoundEvent ITEM_ARMOR_UNEQUIP_WOLF = register("item.armor.unequip_wolf");
    public static final SoundEvent ITEM_WOLF_ARMOR_REPAIR = register("item.wolf_armor.repair");
    public static final SoundEvent ITEM_WOLF_ARMOR_DAMAGE = register("item.wolf_armor.damage");
    public static final SoundEvent ITEM_WOLF_ARMOR_CRACK = register("item.wolf_armor.crack");
    public static final SoundEvent ITEM_WOLF_ARMOR_BREAK = register("item.wolf_armor.break");

    private static SoundEvent register(String id) {
        return register(new Identifier(MOD_ID, id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }
}
