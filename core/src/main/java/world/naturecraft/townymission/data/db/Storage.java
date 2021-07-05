package world.naturecraft.townymission.data.db;

import world.naturecraft.townymission.components.entity.DataEntity;

import java.util.List;

/**
 * The interface Storage.
 *
 * @param <T> the type parameter
 */
public interface Storage<T extends DataEntity> {

    /**
     * Gets entries.
     *
     * @return the entries
     */
    List<T> getEntries();
}
