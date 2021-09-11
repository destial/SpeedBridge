package me.tofpu.speedbridge.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import me.tofpu.speedbridge.SpeedBridge;
import me.tofpu.speedbridge.command.commands.AdminCommand;
import me.tofpu.speedbridge.command.commands.MainCommand;
import me.tofpu.speedbridge.game.Game;
import me.tofpu.speedbridge.game.controller.stage.SetupStage;
import me.tofpu.speedbridge.island.mode.Mode;
import me.tofpu.speedbridge.island.mode.manager.ModeManager;
import me.tofpu.speedbridge.util.Util;

public class CommandHandler {
    private final BukkitCommandManager commandManager;

    public CommandHandler(Game game, final SpeedBridge plugin) {
        commandManager = new BukkitCommandManager(plugin);

        // contexts
        commandManager.getCommandContexts().registerContext(Mode.class, (string) -> {
            final String input = string.popFirstArg();

            return ModeManager.getModeManager().get(input);
        });

        // completions
        commandManager.getCommandCompletions().registerCompletion("modes", context -> Util.toString(ModeManager.getModeManager().getModes()));
        commandManager.getCommandCompletions().registerCompletion("availableIslands", context -> Util.toString(game.getIslandService().getAvailableIslands()));
        commandManager.getCommandCompletions().registerCompletion("setupStage", context -> Util.toString(SetupStage.values()));

        // registrations
        registerCommand(new MainCommand(game.getUserService(), game.getGameService(), game.getLobbyService()));
        registerCommand(new AdminCommand(plugin, game.getLobbyService(), game.getGameService(), game.getGameController()));
    }

    public void registerCommand(final BaseCommand command) {
        commandManager.registerCommand(command);
    }
}
