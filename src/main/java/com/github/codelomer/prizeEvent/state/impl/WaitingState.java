package com.github.codelomer.prizeEvent.state.impl;

import com.github.codelomer.prizeEvent.config.EventConfig;
import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventState;
import com.github.codelomer.prizeEvent.state.EventStatus;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class WaitingState implements EventState {

    private final JavaPlugin plugin;
    private final EventConfig config;
    private BukkitTask task;
    private long eventStartTimeSeconds;
    private BossBar bossBar;
    private boolean updated;
    private String bossBarTitleBefore;
    private String bossBarTitleAfter;

    public WaitingState(JavaPlugin plugin, EventConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void enterState(EventManager eventManager) {
        leaveState(eventManager);
        this.eventStartTimeSeconds = System.currentTimeMillis() / 1000 + eventManager.getEventOverTime();
        createDefaultBossBar();

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        startBossBarAnimation(eventManager);
    }

    private void startBossBarAnimation(EventManager eventManager) {
        long updateThreshold = config.getBossBarUpdateSecondsAfter();

        this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis() / 1000;
            long secondsLeft = eventStartTimeSeconds - now;

            if (secondsLeft <= 0) {
                eventManager.setState(EventStatus.STARTED);
                return;
            }

            // Переключение состояния и начало анимации прогресса
            if (secondsLeft <= updateThreshold && !updated) {
                bossBarTitleAfter = config.getBossBarTitleAfter(); // сохраняем оригинал
                bossBar.setTitle(bossBarTitleAfter); // временно, потом перезапишется PlaceholderAPI
                bossBar.setColor(config.getBossBarColorAfter());
                bossBar.setStyle(config.getBossBarStyleAfter());
                updated = true;
            }

            // Анимация прогресса
            double progress = config.isReversed()
                    ? Math.min(1.0, Math.max(0.0, secondsLeft / (double) updateThreshold))
                    : Math.min(1.0, Math.max(0.0, (updateThreshold - secondsLeft) / (double) updateThreshold));

            bossBar.setProgress(progress);


            // Обновление текста с плейсхолдерами
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!bossBar.getPlayers().contains(player)) {
                    bossBar.addPlayer(player);
                }

                String rawTitle = updated ? bossBarTitleAfter : bossBarTitleBefore;
                String placeholderText = PlaceholderAPI.setPlaceholders(player, rawTitle);
                bossBar.setTitle(placeholderText);
            }
        }, 20L, 20L); // Обновление каждую секунду
    }

    private void createDefaultBossBar() {
        this.bossBarTitleBefore = config.getDefaultBarTitle();
        this.bossBar = Bukkit.createBossBar(config.getDefaultBarTitle(), config.getDefaultColor(), config.getDefaultStyle());
        this.bossBar.setProgress(1.0);
    }

    @Override
    public void leaveState(EventManager eventManager) {
        if (bossBar != null) {
            bossBar.removeAll();
        }
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        eventManager.updateEventOverTime();
        updated = false;
    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.WAITING;
    }

    @Override
    public long timeLeft() {
        return eventStartTimeSeconds - System.currentTimeMillis() / 1000;
    }
}
