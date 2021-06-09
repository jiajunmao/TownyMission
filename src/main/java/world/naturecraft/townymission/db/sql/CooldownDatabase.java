/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.db.sql;

import com.mysql.fabric.xmlrpc.base.Data;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.zaxxer.hikari.HikariDataSource;
import world.naturecraft.townymission.TownyMission;
import world.naturecraft.townymission.components.containers.sql.CooldownEntry;
import world.naturecraft.townymission.components.containers.sql.SeasonEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CooldownDatabase extends Database<CooldownEntry> {
    /**
     * Instantiates a new Database.
     *
     * @param instance  the instance
     * @param db        the db
     * @param tableName the table name
     */
    public CooldownDatabase(TownyMission instance, HikariDataSource db, String tableName) {
        super(instance, db, tableName);
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
            try {
                ResultSet result = p.executeQuery();
                while (result.next()) {
                    try {
                        list.add(new CooldownEntry(result.getInt("id"),
                                result.getString("town_uuid"),
                                result.getInt("cooldown")));
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
        return list;
    }

    public void add(String townUUID, long cooldown) {
        execute(conn -> {
            String sql = "INSERT INTO " + tableName + " VALUES(NULL, '" +
                    townUUID + "', '" +
                    cooldown + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public void remove(int id) {
        execute(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE (" +
                    "id='" + id + "');";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }

    public void update(int id, String townUUID, long cooldown) {
        execute(conn -> {
            String sql = "UPDATE " + tableName +
                    " SET town_uuid='" + townUUID +
                    "', cooldown='" + cooldown +
                    "' WHERE id='" + id + "';";
            PreparedStatement p = conn.prepareStatement(sql);
            p.executeUpdate();
            return null;
        });
    }
}
