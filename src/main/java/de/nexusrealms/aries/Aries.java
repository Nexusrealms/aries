package de.nexusrealms.aries;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Aries implements ModInitializer, EntityComponentInitializer {
	public static final String MOD_ID = "aries";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Identifier id(String name){
		return Identifier.of(MOD_ID, name);
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		PowSet.init();
		CommandRegistrationCallback.EVENT.register(AriesCommands::init);
		LOGGER.info("Hello Fabric world!");
	}
	//TODO make a library which adds this method
	public static <T> Codec<List<T>> mutableListOf(Codec<T> codec){
		return codec.listOf().xmap(ArrayList::new, Function.identity());
	}
	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
		entityComponentFactoryRegistry.registerForPlayers(PowSetComponent.KEY, PowSetComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
	}
	//APIs for when you don't use CCA
	public static Optional<PowSetInstance> addPowSet(PowSetInstance instance, ServerPlayerEntity player){
		return player.getComponent(PowSetComponent.KEY).apply(instance);
	}
	public static Optional<PowSetInstance> removePowSet(RegistryKey<PowSet> key, ServerPlayerEntity player){
		return player.getComponent(PowSetComponent.KEY).remove(key);
	}public static Optional<PowSetInstance> upgradePowSet(RegistryKey<PowSet> key, ServerPlayerEntity player){
		return player.getComponent(PowSetComponent.KEY).upgrade(key);
	}
}