package com.github.codelomer.prizeEvent.integration;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
@RequiredArgsConstructor
public class PlaceholderAPIIntegration extends PlaceholderExpansion {
    private final @NonNull EventManager manager;
    private final @NonNull EventConfig config;
    @Override
    public @NotNull String getIdentifier() {
        return "prize";
    }

    @Override
    public @NotNull String getAuthor() {
        return "codelomer";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String identifier) {
        if(identifier.equalsIgnoreCase("balance")) {
            return String.valueOf(manager.getPrizeAmount());
        }
        if(identifier.equalsIgnoreCase("tickets_amount")){
            return String.valueOf(manager.getTicketsAmount(player.getUniqueId()));
        }
        if(identifier.equalsIgnoreCase("time_left")) {
            long timeLeft = manager.getState().timeLeft();
            return SimpleUtil.parseTime(timeLeft,config.getTimeFormat());
        }
        return null;
    }
}
