package de.nexusrealms.aries;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
public class AriesCommands {
    public static void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(literal("powset")
                .then(argument("players", EntityArgumentType.players())
                        .then(argument("powset", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, PowSet.KEY))
                                .then(literal("apply")
                                        .then(argument("level", IntegerArgumentType.integer(0))
                                                .requires(AriesCommands::mayManipulatePowsets)
                                                .executes(commandContext -> {
                                                    PowSetInstance instance = new PowSetInstance(RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "powset", PowSet.KEY), IntegerArgumentType.getInteger(commandContext, "level"));
                                                    Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(commandContext, "players");
                                                    long succesful = players.stream().filter(player -> player.getComponent(PowSetComponent.KEY).apply(instance).isPresent()).count();
                                                    commandContext.getSource().sendFeedback(() -> Text.translatable("command.aries.apply" + (succesful > 0 ? ".successful" : ".unsuccessful"), green(instance.getId()), green(instance.level()), formattedText(succesful, succesful > 0 ? Formatting.GREEN : Formatting.RED), green(players.size())), true);
                                                    return 1;
                                                })))
                                .then(literal("remove")
                                        .requires(AriesCommands::mayManipulatePowsets)
                                        .executes(commandContext -> {
                                            RegistryKey<PowSet> key = RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "powset", PowSet.KEY).registryKey();
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(commandContext, "players");
                                            long succesful = players.stream().filter(player -> player.getComponent(PowSetComponent.KEY).remove(key).isPresent()).count();
                                            commandContext.getSource().sendFeedback(() -> Text.translatable("command.aries.remove" + (succesful > 0 ? ".successful" : ".unsuccessful"), green(key.getValue()), formattedText(succesful, succesful > 0 ? Formatting.GREEN : Formatting.RED), green(players.size())), true);
                                            return 1;
                                        }))
                                .then(literal("upgrade")
                                        .requires(AriesCommands::mayManipulatePowsets)
                                        .executes(commandContext -> {
                                            RegistryKey<PowSet> key = RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "powset", PowSet.KEY).registryKey();
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(commandContext, "players");
                                            long succesful = players.stream().filter(player -> player.getComponent(PowSetComponent.KEY).upgrade(key).isPresent()).count();
                                            commandContext.getSource().sendFeedback(() -> Text.translatable("command.aries.upgraded" + (succesful > 0 ? ".successful" : ".unsuccessful"), green(key.getValue()), formattedText(succesful, succesful > 0 ? Formatting.GREEN : Formatting.RED), green(players.size())), true);
                                            return 1;
                                        }))
                                .then(literal("check")
                                        .requires(AriesCommands::mayManipulatePowsets)
                                        .executes(commandContext -> {
                                            RegistryKey<PowSet> key = RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "powset", PowSet.KEY).registryKey();
                                            EntityArgumentType.getPlayers(commandContext, "players").forEach(serverPlayer -> {
                                                int level = serverPlayer.getComponent(PowSetComponent.KEY).getLevel(key);
                                                if (level > -1){
                                                    commandContext.getSource().sendFeedback(() -> Text.translatable("command.aries.check.has", serverPlayer.getName().copy().formatted(Formatting.GREEN), green(key.getValue()), green(level)), false);
                                                } else {
                                                    commandContext.getSource().sendFeedback(() -> Text.translatable("command.aries.check.not", serverPlayer.getName().copy().formatted(Formatting.GREEN), green(key.getValue())), false);
                                                }
                                            });
                                            return 1;
                                        })))));
    }
    private static boolean mayManipulatePowsets(ServerCommandSource serverCommandSource){
        return serverCommandSource.hasPermissionLevel(4);
    }
    private static Text green(Object text) {
        return formattedText(text, Formatting.GREEN);
    }
    private static Text formattedText(Object text, Formatting... formatting){
        return Text.literal(text.toString()).formatted(formatting);
    }
}