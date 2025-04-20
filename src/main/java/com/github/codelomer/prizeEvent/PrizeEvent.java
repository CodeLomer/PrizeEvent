package com.github.codelomer.prizeEvent;

import com.github.codelomer.prizeEvent.cmd.AdminCMD;
import com.github.codelomer.prizeEvent.cmd.TicketInformationCMD;
import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.integration.PlaceholderAPIIntegration;
import com.github.codelomer.prizeEvent.integration.VaultAPIIntegration;
import com.github.codelomer.prizeEvent.listener.EventListener;
import com.github.codelomer.prizeEvent.manager.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrizeEvent extends JavaPlugin {

    @Override
    public void onEnable() {
        EventConfig config = new EventConfig(this);
        VaultAPIIntegration vaultAPI = new VaultAPIIntegration();
        EventManager eventManager = new EventManager(config,this,vaultAPI.getEconomy());

        Bukkit.getPluginManager().registerEvents(new EventListener(eventManager, config), this);
        new AdminCMD("prizeeventAdmin",this,eventManager,config);
        new TicketInformationCMD("prizeevent",this,config);
        new PlaceholderAPIIntegration(eventManager,config).register();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
