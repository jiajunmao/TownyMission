package world.naturecraft.townymission.core.data.db;

import world.naturecraft.townymission.core.components.entity.CooldownEntry;
import world.naturecraft.townymission.core.components.entity.DataEntity;

import java.util.List;

public interface Storage<T extends DataEntity> {

    List<T> getEntries();
}
