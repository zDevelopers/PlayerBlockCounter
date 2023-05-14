package fr.zcraft.PlayerBlockCounter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class BlockListeners implements Listener {
    private final Main plugin;

    public BlockListeners(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        int count = plugin.getPlayerBreakCount(playerUUID);
        plugin.setPlayerBreakCount(playerUUID, count + 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        int count = plugin.getPlayerPlaceCount(playerUUID);
        plugin.setPlayerPlaceCount(playerUUID, count + 1);
    }
}
