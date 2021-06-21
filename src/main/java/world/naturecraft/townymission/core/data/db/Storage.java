package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.DataEntity;

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
