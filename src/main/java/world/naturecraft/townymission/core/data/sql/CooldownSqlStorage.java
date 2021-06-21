/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.data.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.core.components.entity.CooldownEntry;
import world.naturecraft.townymission.core.components.enums.DbType;
import world.naturecraft.townymission.core.data.db.CooldownStorage;
import world.naturecraft.townymission.core.services.StorageService;
import world.naturecraft.townymission.core.utils.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
        List<CooldownEntry> list = new ArrayList<>();
        execute(conn -> {
            String sql = "SELECT * FROM " + tableName + ";";
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet result = p.executeQuery();
            while (result.next()) {
                try {
                    list.add(new CooldownEntry(UUID.fromString(result.getString("uuid")),
                            UUID.fromString(result.getString("town_uuid")),
                            result.getString("cooldownjsonlist")));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
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
     * Remove.
     *
     * @param uuid the id
     */
    public void remove(UUID uuid) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "uuid='" + uuid.toString() + "');";
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
}
