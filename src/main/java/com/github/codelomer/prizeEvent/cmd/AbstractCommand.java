package com.github.codelomer.prizeEvent.cmd;

import lombok.NonNull;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements CommandExecutor, TabExecutor {

    public AbstractCommand(@NonNull String command, @NonNull JavaPlugin plugin){
        PluginCommand pluginCommand = plugin.getCommand(command);
        if(pluginCommand != null){
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        execute(commandSender,s,strings);
        return true;
    }

    protected abstract void execute(CommandSender sender, String alias, String[] args);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> options = getTabOptions(sender, alias, args);
        if (args.length == 0 || options.isEmpty()) return options;
        String currentInput = args[args.length - 1].toLowerCase();
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(currentInput))
                .collect(Collectors.toList());
    }
    protected List<String> getTabOptions(CommandSender sender, String alias, String[] args) {
        return Collections.emptyList();
    }
}
