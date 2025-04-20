package com.github.codelomer.prizeEvent.cmd;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class TicketInformationCMD extends AbstractCommand{
    @NonNull
    private final EventConfig config;

    public TicketInformationCMD(@NonNull String command, @NonNull JavaPlugin plugin, @NonNull EventConfig config) {
        super(command, plugin);
        this.config = config;
    }

    @Override
    protected void execute(CommandSender sender, String alias, String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("info")){
            if(!(sender instanceof Player player)) return;
            SimpleUtil.sendMessage(player, config.getMessage("player-info").stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).toList());
            return;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("help")){
            SimpleUtil.sendMessage(sender, config.getMessage("player-help"));
            return;
        }
        SimpleUtil.sendMessage(sender, config.getMessage("invalid-arguments"));
    }

    @Override
    protected List<String> getTabOptions(CommandSender sender, String alias, String[] args) {
        if(args.length == 1){
            return List.of("info", "help");
        }
        return Collections.emptyList();
    }
}
