package uk.co.forgottendream.vfbackports.world.event;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class ModGameEvent {
    public static final GameEvent ENTITY_ACTION = register("entity_action");

    private static GameEvent register(String id) {
        return register(id, 16);
    }

    private static GameEvent register(String id, int range) {
        return Registry.register(Registries.GAME_EVENT, id, new GameEvent(id, range));
    }
}
