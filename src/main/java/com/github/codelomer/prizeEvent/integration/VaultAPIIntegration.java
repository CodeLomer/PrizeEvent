package com.github.codelomer.prizeEvent.integration;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
@Getter
public class VaultAPIIntegration {
    private final Economy economy;

    public VaultAPIIntegration() {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);

        if (provider != null) {
            this.economy = provider.getProvider();
        } else {
            economy = null;
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"[PrizeEvent] провайдер экономики для VaultAPI не найден.");
        }
    }
}
