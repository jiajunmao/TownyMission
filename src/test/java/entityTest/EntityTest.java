package entityTest;

import com.palmergames.bukkit.towny.object.Town;
import org.junit.Test;
import world.naturecraft.townymission.components.entity.CooldownEntry;
import world.naturecraft.townymission.components.entity.SprintEntry;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class EntityTest {

    @Test
    public void sprintEntry_constructorTest() {
        SprintEntry sprintEntry = new SprintEntry(UUID.randomUUID(), UUID.randomUUID().toString(), "TestDummy" ,73808, 73808, 73808);

        assertEquals(sprintEntry.getSeason(), 73808);
    }
}
