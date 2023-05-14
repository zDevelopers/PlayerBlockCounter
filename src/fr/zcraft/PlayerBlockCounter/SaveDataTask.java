package fr.zcraft.PlayerBlockCounter;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;

public class SaveDataTask extends BukkitRunnable
{
    private final Main plugin;

    public SaveDataTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // Resets the value at midnight
        if (now.getHour() == 0 && now.getMinute() == 0)
            plugin.clearStatsMaps();

        String statsFileName = "stats-%d-%d-%d.json".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String statsJSONString = plugin.getStatsJSONString() + "\n";

        File dir = plugin.getDataFolder();
        String dirPath = dir.getAbsolutePath();
        File statsFile = new File(dir, statsFileName);
        String statsFilePath = statsFile.getAbsolutePath();

        if (!dir.exists()) {
            boolean success = dir.mkdir();
            if (!success) return;
        }

        if (!statsFile.exists()) {
            try {
                boolean success = statsFile.createNewFile();
                if (!success) return;
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Couldn't create the file '" + statsFilePath + "': " + e.getMessage());
            }
        }

        try (FileWriter writer = new FileWriter(statsFile, false)) {
            writer.write(statsJSONString);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "An error occurred while writing to the file '" + statsFilePath + "': " + e.getMessage());
        }
    }
}
