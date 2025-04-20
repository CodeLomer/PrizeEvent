package com.github.codelomer.prizeEvent.state.impl;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventState;
import com.github.codelomer.prizeEvent.state.EventStatus;
import com.github.codelomer.prizeEvent.model.EventTicket;
import com.github.codelomer.prizeEvent.util.SimpleUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
@RequiredArgsConstructor
public class StartedState implements EventState {
    private final @NonNull EventConfig config;
    private final Economy economy;

    @Override
    public void enterState(EventManager eventManager) {
        Set<EventTicket> tickets = eventManager.getTickets();

        if (tickets.isEmpty()) {
            for(String line: config.getMessage("no-one-payed")) {
                Bukkit.broadcastMessage(line);
            }
            eventManager.setState(EventStatus.WAITING);
            return;
        }

       OfflinePlayer winner = getRandomWinner(tickets, eventManager);

        if (winner == null) {
            eventManager.setState(EventStatus.WAITING);
            return;
        }

        // TODO: Показать GUI анимацию игрокам
        for (Player player : Bukkit.getOnlinePlayers()) {

        }

        if(economy != null){
            economy.depositPlayer(winner, eventManager.getPrizeAmount());
        }
        for(Player player: Bukkit.getOnlinePlayers()) {
            SimpleUtil.sendMessage(player, config.getMessage("winner").stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).toList());
        }
        eventManager.setState(EventStatus.WAITING);
    }

    @Override
    public void leaveState(EventManager eventManager) {
    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.STARTED;
    }

    @Override
    public long timeLeft() {
        return 0;
    }

    private OfflinePlayer getRandomWinner(@NonNull Set<EventTicket> tickets, @NonNull EventManager eventManager) {
        int randomIndex = (int) (Math.random() * tickets.size());
        EventTicket winnerTicket = tickets.stream().skip(randomIndex).findFirst().orElse(null);

        if (winnerTicket == null) {
            Bukkit.broadcastMessage("§cПроизошла неизвестная ошибка.");
            eventManager.setState(EventStatus.WAITING);
            return null;
        }
        tickets.remove(winnerTicket);
        return Bukkit.getOfflinePlayer(winnerTicket.getOwner());
    }
}
