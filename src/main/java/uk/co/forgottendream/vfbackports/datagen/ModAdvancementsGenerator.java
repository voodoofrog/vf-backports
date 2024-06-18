package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import uk.co.forgottendream.vfbackports.entity.ModEntityType;
import uk.co.forgottendream.vfbackports.item.ModItems;
import uk.co.forgottendream.vfbackports.predicate.entity.ModDeserializers;
import uk.co.forgottendream.vfbackports.registry.ModRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.data.server.advancement.vanilla.VanillaHusbandryTabAdvancementGenerator.*;

public class ModAdvancementsGenerator extends FabricAdvancementProvider {
    protected ModAdvancementsGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement adventureRoot = new Advancement(new Identifier("adventure/root"), null, null, AdvancementRewards.NONE, new HashMap<>(), null, false);
        Advancement tameAnimal = new Advancement(new Identifier("husbandry/tame_an_animal"), null, null, AdvancementRewards.NONE, new HashMap<>(), null, false);
        Advancement breedAnimal = new Advancement(new Identifier("husbandry/breed_an_animal"), null, null, AdvancementRewards.NONE, new HashMap<>(), null, false);
        Advancement.Builder.create()
                .parent(adventureRoot)
                .display(
                        ModItems.ARMADILLO_SCUTE,
                        Text.translatable("advancements.adventure.brush_armadillo.title"),
                        Text.translatable("advancements.adventure.brush_armadillo.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                ).criterion("brush_armadillo", PlayerInteractedWithEntityCriterion.Conditions.create(
                        ItemPredicate.Builder.create().items(Items.BRUSH),
                        EntityPredicate.asLootContextPredicate(EntityPredicate.Builder.create().type(ModEntityType.ARMADILLO).build())
                ))
                .build(consumer, "adventure/brush_armadillo");
        Advancement.Builder.create()
                .parent(tameAnimal)
                .display(
                        Items.SHEARS,
                        Text.translatable("advancements.husbandry.remove_wolf_armor.title"),
                        Text.translatable("advancements.husbandry.remove_wolf_armor.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                ).criterion("remove_wolf_armor", PlayerInteractedWithEntityCriterion.Conditions.create(
                        ItemPredicate.Builder.create().items(Items.SHEARS),
                        EntityPredicate.asLootContextPredicate(EntityPredicate.Builder.create().type(EntityType.WOLF).build())
                ))
                .build(consumer, "husbandry/remove_wolf_armor");
        Advancement.Builder.create()
                .parent(tameAnimal)
                .display(
                        ModItems.WOLF_ARMOR,
                        Text.translatable("advancements.husbandry.repair_wolf_armor.title"),
                        Text.translatable("advancements.husbandry.repair_wolf_armor.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                ).criterion("repair_wolf_armor", PlayerInteractedWithEntityCriterion.Conditions.create(
                        ItemPredicate.Builder.create().items(ModItems.ARMADILLO_SCUTE),
                        EntityPredicate.asLootContextPredicate(EntityPredicate.Builder.create().type(EntityType.WOLF).build())
                ))
                .build(consumer, "husbandry/repair_wolf_armor");
        Advancement.Builder.create()
                .parent(tameAnimal)
                .display(
                        Items.BONE,
                        Text.translatable("advancements.husbandry.whole_pack.title"),
                        Text.translatable("advancements.husbandry.whole_pack.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                ).criterion("vf-backports:ashen", PlayerInteractedWithEntityCriterion.Conditions.create(
                        ItemPredicate.Builder.create().items(ModItems.ARMADILLO_SCUTE),
                        EntityPredicate.asLootContextPredicate(EntityPredicate.Builder.create().type(EntityType.WOLF).build())
                ))
                .build(consumer, "husbandry/repair_wolf_armor");

        ModAdvancementsGenerator.requireAllWolvesTamed(Advancement.Builder.create())
                .parent(tameAnimal)
                .display(
                        Items.BONE,
                        Text.translatable("advancements.husbandry.whole_pack.title"),
                        Text.translatable("advancements.husbandry.whole_pack.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(50))
                .build(consumer, "husbandry/whole_pack");

        createBreedAllAnimalsAdvancement(breedAnimal, consumer, BREEDABLE_ANIMALS.stream(), EGG_LAYING_ANIMALS.stream());

    }

    private static Advancement.Builder requireAllWolvesTamed(Advancement.Builder builder) {
        ModRegistries.WOLF_VARIANTS.getEntrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(RegistryKey::getValue))).forEach((entry) -> {
            builder.criterion((entry.getKey()).getValue().toString(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().typeSpecific(ModDeserializers.wolf(entry.getValue())).build()));
        });
        return builder;
    }
}
