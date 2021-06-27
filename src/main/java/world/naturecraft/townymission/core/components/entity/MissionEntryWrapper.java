package world.naturecraft.townymission.core.components.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.inventory.ItemStack;
import world.naturecraft.townymission.core.components.enums.MissionType;
import world.naturecraft.townymission.core.components.json.mission.MissionJson;

import java.util.UUID;

public interface MissionEntryWrapper {

    long getAddedTime();

    MissionType getMissionType();

    long getStartedTime();

    void setStartedTime(long startedTime);

    MissionJson getMissionJson();

    void setMissionJson(MissionJson json) throws JsonProcessingException;

    UUID getTownUUID();

    String getDisplayLine() throws JsonProcessingException;

    long getAllowedTime();

    UUID getStartedPlayerUUID();

    void setStartedPlayerUUID(UUID startedPlayerUUID);

    ItemStack getGuiItem();

    boolean isStarted();

    boolean isTimedout();

    boolean isCompleted();
}
