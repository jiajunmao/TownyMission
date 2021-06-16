package world.naturecraft.townymission.bukkit.utils;

import world.naturecraft.townymission.core.components.entity.DataEntity;

/**
 * The interface Entry filter.
 *
 * @param <T> the type parameter
 */
public interface EntryFilter<T extends DataEntity> {

    /**
     * Include boolean.
     *
     * @param data the data
     * @return the boolean
     */
    boolean include(T data);
}
