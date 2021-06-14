package world.naturecraft.townymission.utils;

import world.naturecraft.townymission.components.entity.DataEntity;

public interface EntryFilter<T extends DataEntity> {

    boolean include(T data);
}
