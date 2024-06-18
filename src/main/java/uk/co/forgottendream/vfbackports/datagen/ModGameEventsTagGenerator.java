package uk.co.forgottendream.vfbackports.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.GameEvent;
import uk.co.forgottendream.vfbackports.world.event.ModGameEvent;

import java.util.concurrent.CompletableFuture;

public class ModGameEventsTagGenerator extends FabricTagProvider.GameEventTagProvider {
    public static final TagKey<GameEvent> VIBRATIONS = TagKey.of(RegistryKeys.GAME_EVENT, new Identifier("vibrations"));
    public static final TagKey<GameEvent> WARDEN_CAN_LISTEN = TagKey.of(RegistryKeys.GAME_EVENT, new Identifier("warden_can_listen"));

    public ModGameEventsTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(VIBRATIONS)
                .add(ModGameEvent.ENTITY_ACTION);

        getOrCreateTagBuilder(WARDEN_CAN_LISTEN)
                .add(ModGameEvent.ENTITY_ACTION);
    }
}
