package world.naturecraft.townymission.core.services;

import com.Zrips.CMI.Modules.DataBase.DBDAO;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import world.naturecraft.townymission.bukkit.TownyMission;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.components.enums.StorageType;
import world.naturecraft.townymission.core.data.db.Storage;
import world.naturecraft.townymission.core.data.sql.*;
import world.naturecraft.townymission.core.data.yaml.*;

import java.util.HashMap;
import java.util.Map;

public class StorageService {

    private static StorageService singleton;
    private StorageType storageType;
    private Map<DbType, Storage> dbMap;
    private TownyMission instance;

    public StorageService() {
        TownyMission instance = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        this.instance = instance;
        this.storageType = instance.getStorageType();
        dbMap = new HashMap<>();
        initializeMap();
    }


    public static StorageService getInstance() {
        if (singleton == null) {
            singleton = new StorageService();
        }

        return singleton;
    }

    @SuppressWarnings("unchecked")
    public <T extends Storage> T getStorage(DbType dbType) {
        TownyMission townyMission = (TownyMission) Bukkit.getPluginManager().getPlugin("TownyMission");
        return (T) dbMap.get(dbType);
    }

    public void initializeMap() {
        for (DbType dbType : DbType.values()) {
            switch (dbType) {
                case SEASON:
                    if (storageType.equals(StorageType.MYSQL))
                        dbMap.put(DbType.SEASON, SeasonSqlStorage.getInstance());
                    else
                        dbMap.put(DbType.SEASON, SeasonYamlStorage.getInstance());
                    break;
                case SPRINT:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SPRINT, SprintYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SPRINT, SprintSqlStorage.getInstance());
                    break;
                case CLAIM:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.CLAIM, ClaimYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.CLAIM, ClaimSqlStorage.getInstance());
                case MISSION:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION, MissionYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION, MissionSqlStorage.getInstance());
                case COOLDOWN:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.COOLDOWN, CooldownYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.COOLDOWN, CooldownSqlStorage.getInstance());
                case SEASON_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SEASON_HISTORY, SeasonHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SEASON_HISTORY, SeasonHistorySqlStorage.getInstance());
                case SPRINT_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.SPRINT_HISTORY, SprintHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.SPRINT_HISTORY, SprintHistorySqlStorage.getInstance());
                case MISSION_HISTORY:
                    if (storageType.equals(StorageType.YAML))
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistoryYamlStorage.getInstance());
                    else
                        dbMap.put(DbType.MISSION_HISTORY, MissionHistorySqlStorage.getInstance());
            }
        }
    }

}
