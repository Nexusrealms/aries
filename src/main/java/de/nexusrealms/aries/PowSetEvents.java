package de.nexusrealms.aries;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PowSetEvents {
    Event<ApplyPowSetCallback> APPLY = EventFactory.createArrayBacked(ApplyPowSetCallback.class, (callbacks) -> (instance, player, fromUpgrade, isNew) -> {
        for(ApplyPowSetCallback callback : callbacks) {
            callback.call(instance, player, fromUpgrade, isNew);
        }
    });
    Event<RemovePowSetCallback> REMOVE = EventFactory.createArrayBacked(RemovePowSetCallback.class, (callbacks) -> (instance, player) -> {
        for(RemovePowSetCallback callback : callbacks) {
            callback.call(instance, player);
        }
    });
    @FunctionalInterface
    interface ApplyPowSetCallback{
        void call(PowSetInstance instance, ServerPlayerEntity player, boolean fromUpgrade, boolean isNew);
    }
    @FunctionalInterface
    interface RemovePowSetCallback{
        void call(PowSetInstance instance, ServerPlayerEntity player);
    }
}
