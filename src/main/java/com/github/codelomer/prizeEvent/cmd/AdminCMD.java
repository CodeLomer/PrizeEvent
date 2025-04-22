package com.github.codelomer.prizeEvent.cmd;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventStatus;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class AdminCMD extends AbstractCommand {
    @NonNull
    private final EventManager eventManager;
    @NonNull
    private final EventConfig config;

    public AdminCMD(@NonNull String command, @NonNull JavaPlugin plugin, @NonNull EventManager eventManager, @NonNull EventConfig config) {
        super(command, plugin);
        this.eventManager = eventManager;
        this.config = config;
    }

    @Override
    protected void execute(CommandSender sender, String alias, String[] args) {
        if(args.length == 3 && args[0].equalsIgnoreCase("give")){
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                SimpleUtil.sendMessage(sender,config.getMessage("not-online"));
                return;

            }
            int amount = getNumber(args[2]);
            if(amount < 1){
                SimpleUtil.sendMessage(sender,config.getMessage("invalid-amount"));
                return;
            }
            eventManager.giveTicket(player.getUniqueId(),amount,true);
            SimpleUtil.sendMessage(sender,config.getMessage("ticket-given"));
            return;
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("take")){
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null){
                SimpleUtil.sendMessage(sender,config.getMessage("not-online"));
                return;

            }
            int amount = getNumber(args[2]);
            if(amount < 1){
                SimpleUtil.sendMessage(sender,config.getMessage("invalid-amount"));
                return;
            }
            eventManager.removeTicket(player.getUniqueId(),amount);
            SimpleUtil.sendMessage(sender,config.getMessage("ticket-taken"));
            return;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
            config.reloadConfig();
            eventManager.reload();
            SimpleUtil.sendMessage(sender,config.getMessage("config-reloaded"));
            return;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("info")){
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null){
                SimpleUtil.sendMessage(sender,config.getMessage("not-online"));
                return;
            }
            SimpleUtil.sendMessage(sender,config.getMessage("player-info-for-admin").stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).toList());
            return;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("help")){
            SimpleUtil.sendMessage(sender,config.getMessage("admin-help"));
            return;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("start")){
            eventManager.setState(EventStatus.WAITING);
            SimpleUtil.sendMessage(sender,config.getMessage("event-started"));
            return;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("skip")){
            eventManager.setState(EventStatus.STARTED);
            SimpleUtil.sendMessage(sender,config.getMessage("event-skipped"));
            return;

        }
        SimpleUtil.sendMessage(sender,config.getMessage("invalid-arguments"));
    }

    private int getNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    protected List<String> getTabOptions(CommandSender sender, String alias, String[] args){
        if(args.length == 1){
            return List.of("give","take","reload","info","help","start","skip");
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("info")){
            return SimpleUtil.toColorListText(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("give") || args.length == 3 && args[0].equalsIgnoreCase("take")){
            return SimpleUtil.toColorListText(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("give") || args.length == 4 && args[0].equalsIgnoreCase("take")){
            return List.of("<количество>");
        }
        return Collections.emptyList();
    }


}
