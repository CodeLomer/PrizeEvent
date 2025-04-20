package com.github.codelomer.prizeEvent.listener;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
@RequiredArgsConstructor
public class EventListener implements Listener {
    private final @NonNull EventManager manager;
    private final @NonNull EventConfig config;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore() && manager.getState().getStatus() == EventStatus.WAITING){
            manager.setPrizeAmount(config.getBalanceGive());
        }
    }
}
