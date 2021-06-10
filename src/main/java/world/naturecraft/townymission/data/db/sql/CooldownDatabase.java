/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.data.db.sql;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.components.containers.sql.CooldownEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Cooldown database.
 */
public class CooldownDatabase extends Database<CooldownEntry> {

    private static CooldownDatabase singleton = null;

    /**
     * Instantiates a new Database.
     *
     * @param db        the db
     * @param tableName the table name
     */
    public CooldownDatabase(HikariDataSource db, String tableName) {
        super(db, tableName);
        singleton = this;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CooldownDatabase getInstance() {
        return singleton;
    }

    /**
     * Create table.
     */
    @Override
    public void createTable() {
        execute(conn -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                    "`id` INT NOT NULL AUTO_INCREMENT ," +
                    "`town_uuid` VARCHAR(255) NOT NULL ," +
                    "`started_time` BIGINT NOT NULL," +
                    "`cooldown` BIGINT NOT NULL," +
                    "PRIMARY KEY (`id`))";
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
                    list.add(new CooldownEntry(result.getInt("id"),
                            result.getString("town_uuid"),
                            result.getLong("started_time"),
                            result.getLong("cooldown")));
                } catch (NotRegisteredException e) {
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
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void add(String townUUID, long startedTime, long cooldown) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    townUUID + "', '" +
                    startedTime + "', '" +
                    cooldown + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Remove.
     *
     * @param id the id
     */
    public void remove(int id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    /**
     * Update.
     *
     * @param id          the id
     * @param townUUID    the town uuid
     * @param startedTime the started time
     * @param cooldown    the cooldown
     */
    public void update(int id, String townUUID, long startedTime, long cooldown) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_uuid='" + townUUID +
                    "', started_time='" + startedTime +
                    "', cooldown='" + cooldown +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
