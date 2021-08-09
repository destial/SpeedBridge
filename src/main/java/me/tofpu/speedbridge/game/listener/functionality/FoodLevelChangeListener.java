package me.tofpu.speedbridge.game.listener.functionality;

import me.tofpu.speedbridge.game.service.IGameService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    private final IGameService gameService;

    public FoodLevelChangeListener(final IGameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler(ignoreCancelled = true)
    private void onFoodLevelChange(final FoodLevelChangeEvent event){
        if (!(event.getEntity() instanceof Player)) return;

        final Player player = (Player) event.getEntity();
        if (gameService.isPlaying(player)) event.setCancelled(true);
    }
}