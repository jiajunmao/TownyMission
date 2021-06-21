package world.naturecraft.townymission.core.config;

import java.io.IOException;
import java.util.Collection;

public interface TownyMissionConfig {

    void createConfig(String path);

    void updateConfig(String path);

    int getInt(String path);

    String getString(String path);

    double getDouble(String path);

    boolean getBoolean(String path);

    long getLong(String path);

    Collection<String> getShallowKeys();

    void set(String path, Object content);

    void save() throws IOException;
}
