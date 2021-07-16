/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.source.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.scheduler.BukkitRunnable;
import world.naturecraft.townymission.TownyMissionInstance;
import world.naturecraft.townymission.components.entity.ClaimEntry;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.enums.DbType;
import world.naturecraft.townymission.data.storage.CooldownStorage;
import world.naturecraft.townymission.services.StorageService;
import world.naturecraft.townymission.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Cooldown database.
 */
public class CooldownSqlStorage extends SqlStorage<CooldownEntry> implements CooldownStorage {

    private static CooldownSqlStorage singleton;

    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public CooldownSqlStorage(HikariDataSource db, String tableName) {
        super(db, tableName);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownSqlStorage getInstance() {
        if (singleton == null) {
            singleton = new CooldownSqlStorage(
                    StorageService.getInstance().getDataSource(),
                    Util.getDbName(DbType.COOLDOWN));
        }
        return singleton;
    }

    /**
     * Create table.
     */
    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`uuid` VARCHAR(255) NOT NULL," +
                    "`town_uuid` VARCHAR(255) NOT NULL ," +
                    "`cooldownjsonlist` VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (`uuid`))";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    @Override
    public List<CooldownEntry> getEntries() {
        if (cached) {
            List<CooldownEntry> list = new ArrayList<>(memCache.values());
            return list;
        }

        List<CooldownEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                list.add(new CooldownEntry(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("town_uuid")),
                        result.getString("cooldownjsonlist")));
            }
            return null;
        });
        return list;
    }

    /**
     * Add.
     *
     * @param townUUID the town uuid
     */
    public void add(UUID townUUID, String cooldownJsonList) {
        if (cached) {
            CooldownEntry cooldownEntry = new CooldownEntry(UUID.randomUUID(), townUUID, cooldownJsonList);
            memCache.put(cooldownEntry.getId(), cooldownEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    addRemote(townUUID, cooldownJsonList);
                }
            };
            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        addRemote(townUUID, cooldownJsonList);
    }

    private void addRemote(UUID townUUID, String cooldownJsonList) {
        execute(conn -> {
            UUID uuid = UUID.randomUUID();
            String sql = "INSERT INTO " + tableName + " VALUES('" + uuid + "', '" +
                    townUUID + "', '" +
                    cooldownJsonList + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param uuid     the id
     * @param townUUID the town uuid
     */
    public void update(UUID uuid, UUID townUUID, String cooldownJsonList) {
        if (cached) {
            CooldownEntry cooldownEntry = new CooldownEntry(uuid, townUUID, cooldownJsonList);
            memCache.put(cooldownEntry.getId(), cooldownEntry);
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    updateRemote(uuid, townUUID, cooldownJsonList);
                }
            };

            r.runTaskAsynchronously(TownyMissionInstance.getInstance());
            return;
        }

        updateRemote(uuid, townUUID, cooldownJsonList);
    }

    private void updateRemote(UUID uuid, UUID townUUID, String cooldownJsonList) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_uuid='" + townUUID +
                    "', cooldownjsonlist='" + cooldownJsonList +
                    "' WHERE uuid='" + uuid + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public void update(CooldownEntry data) {
        update(data.getId(), data.getTownUUID(), data.getCooldownsAsString());
    }
}
