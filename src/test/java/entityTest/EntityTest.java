package entityTest;

import org.junit.Test;
import world.naturecraft.townymission.core.components.entity.SprintEntry;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class EntityTest {

    @Test
    public void sprintEntry_constructorTest() {
        SprintEntry sprintEntry = new SprintEntry(UUID.randomUUID(), UUID.randomUUID(),73808, 73808, 73808);

        assertEquals(sprintEntry.getSeason(), 73808);
    }
}
