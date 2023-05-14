package fr.zcraft.PlayerBlockCounter;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        try {
            Objects.requireNonNull(getCommand("blockstats")).setExecutor(new BlockStatsCommand(this));
        } catch (NullPointerException e) {
            getLogger().log(Level.WARNING, "Couldn't load command 'blockstats': " + e.getMessage());
        }

        readDataFromFile();

        BukkitRunnable saveDataTask = new SaveDataTask(this);
        saveDataTask.runTaskTimerAsynchronously(this, 20L * 60L, 20L * 60L);
    }

    private final ConcurrentHashMap<UUID, Integer> playerBreakCountMap = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<UUID, Integer> playerPlaceCountMap = new ConcurrentHashMap<>();

    public int getPlayerBreakCount(UUID playerUUID) {
        return playerBreakCountMap.getOrDefault(playerUUID, 0);
    }

    public int getPlayerPlaceCount(UUID playerUUID) {
        return playerPlaceCountMap.getOrDefault(playerUUID, 0);
    }

    public void setPlayerBreakCount(UUID playerUUID, int count) {
        playerBreakCountMap.put(playerUUID, count);
    }

    public void setPlayerPlaceCount(UUID playerUUID, int count) {
        playerPlaceCountMap.put(playerUUID, count);
    }

    public void clearPlayerBreakCountMap() {
        playerBreakCountMap.clear();
    }

    public void clearPlayerPlaceCountMap() {
        playerPlaceCountMap.clear();
    }

    public void clearStatsMaps() {
        clearPlayerBreakCountMap();
        clearPlayerPlaceCountMap();
    }

    private JSONObject mapToJSON(ConcurrentHashMap<UUID, Integer> map) {
        JSONObject json = new JSONObject();

        for (Map.Entry<UUID, Integer> entry : map.entrySet())
            json.put(entry.getKey().toString(), entry.getValue());

        return json;
    }

    public JSONObject getPlayerBreakCountMapJSON() {
        return mapToJSON(playerBreakCountMap);
    }

    public JSONObject getPlayerPlaceCountMapJSON() {
        return mapToJSON(playerPlaceCountMap);
    }

    public String getStatsJSONString() {
        JSONObject json = new JSONObject();
        json.put("break", getPlayerBreakCountMapJSON());
        json.put("place", getPlayerPlaceCountMapJSON());

        return json.toString();
    }

    private void readDataFromFile() {
        clearStatsMaps();

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        String statsFileName = "stats-%d-%d-%d.json".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        File dir = getDataFolder();
        File statsFile = new File(dir, statsFileName);

        getLogger().log(Level.INFO, "Loading data from file '" + statsFile.getAbsolutePath() + "'");

        if (!dir.exists()) return;
        if (!statsFile.exists()) return;

        try {
            String statsFileContent = new String(Files.readAllBytes(Paths.get(statsFile.getAbsolutePath())));
            JSONObject statsJSON = new JSONObject(statsFileContent);

            JSONObject breakJSON = statsJSON.getJSONObject("break");
            for (String playerUUIDString : breakJSON.keySet()) {
                int count = breakJSON.getInt(playerUUIDString);
                playerBreakCountMap.put(UUID.fromString(playerUUIDString), count);
            }

            JSONObject placeJSON = statsJSON.getJSONObject("place");
            for (String playerUUIDString : placeJSON.keySet()) {
                int count = placeJSON.getInt(playerUUIDString);
                playerPlaceCountMap.put(UUID.fromString(playerUUIDString), count);
            }
        } catch (IOException ignored) {}
    }
}