package fr.zcraft.PlayerBlockCounter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BlockStatsCommand implements CommandExecutor {
    private final Main plugin;

    public BlockStatsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            UUID playerUUID = player.getUniqueId();
            int breakCount = plugin.getPlayerBreakCount(playerUUID);
            int placeCount = plugin.getPlayerPlaceCount(playerUUID);
            String breakBlockString = "bloc";
            String placeBlockString = "bloc";
            if (breakCount > 1) breakBlockString += "s";
            if (placeCount > 1) placeBlockString += "s";
            player.sendMessage("Aujourd'hui, vous avez cassé %d %s et placé %d %s.".formatted(breakCount, breakBlockString, placeCount, placeBlockString));
        } else {
            sender.sendMessage("Seuls les joueurs peuvent utiliser cette commande !");
        }

        return true;
    }
}
