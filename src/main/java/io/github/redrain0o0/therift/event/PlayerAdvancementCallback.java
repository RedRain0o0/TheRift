package io.github.redrain0o0.therift.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerAdvancementCallback {
    Event<PlayerAdvancementCallback> EVENT = EventFactory.createArrayBacked(PlayerAdvancementCallback.class, (listeners) -> (player, advancement) -> {
        for (PlayerAdvancementCallback listener : listeners) {
            listener.onAdvancementGranted(player, advancement);
        }
    });

    void onAdvancementGranted(ServerPlayer playerEntity, Advancement advancement);
}
