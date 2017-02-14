package de.tomgrill.gdxtesting.examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.map.thread.Generator;

import java.util.HashMap;
import java.util.HashSet;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class AssetExistsExampleTest {

	@Test
	public void getAdjZone() {
		Zombies instance = new Zombies();
		instance.setScreen(new GameView());
        GameView.gv.reset();

        Zone zone = Zone.getZone(0, 0);
        Room room = Generator.genRoom(new Building(new Vector2(300, 300)), "0,0");

        // assert a room is generating
		assertTrue(zone.getRooms().size() > 0);

        // assert getAdjZones works
        HashSet<Zone> adjZones = zone.getAdjZones(1);
        HashMap<String, Zone> adjZonesMap = new HashMap<String, Zone>();
        assertTrue(adjZones.size() == 9);

        for (Zone z : adjZones)
            adjZonesMap.put(z.getKey(), z);

        assertTrue(adjZonesMap.get("-1,-1") != null);
        assertTrue(adjZonesMap.get("0,-1")  != null);
        assertTrue(adjZonesMap.get("1,-1")  != null);
        assertTrue(adjZonesMap.get("-1,0")  != null);
        assertTrue(adjZonesMap.get("0,0")   != null);
        assertTrue(adjZonesMap.get("1,0")   != null);
        assertTrue(adjZonesMap.get("-1,1")  != null);
        assertTrue(adjZonesMap.get("0,1")   != null);
        assertTrue(adjZonesMap.get("1,1")   != null);

        adjZones = zone.getAdjZones(2);
        assertTrue(adjZones.size() == 25);
    }

    @Test
    public void testRoomZoning() {
        GameView.gv.reset();

        Zone z1 = Zone.getZone(0, 0);
        Zone z2 = Zone.getZone(-1, 0);
        Zone z3 = Zone.getZone(-1, -1);
        Zone z4 = Zone.getZone(0, -1);

        assertTrue(z1.getRooms().size() == 0);
        assertTrue(z2.getRooms().size() == 0);
        assertTrue(z3.getRooms().size() == 0);
        assertTrue(z4.getRooms().size() == 0);

        Building building = new Building(new Vector2(0, 0));
        Room room = new Room(building);

        Box b00  = new Box(building, room, "0,0");
        Box b10  = new Box(building, room, "1,0");
        Box bn10 = new Box(building, room, "-1,0");
        Box b01  = new Box(building, room, "0,1");
        Box b0n1 = new Box(building, room, "0,-1");

        room.finalize();

        // zone 1
        assertTrue(room.getZone() == z1);
        assertTrue(z1.getRooms().size() == 1);
        assertTrue(z1.getBoxes().size() == 3);
        assertTrue(z1.getBoxes().contains(building.boxMap.get("0,0")));
        assertTrue(z1.getBoxes().contains(building.boxMap.get("1,0")));
        assertTrue(z1.getBoxes().contains(building.boxMap.get("0,1")));

        // zone 2
        assertTrue(z2.getRooms().size() == 1);
        assertTrue(z2.getBoxes().size() == 3);
        assertTrue(z2.getBoxes().contains(building.boxMap.get("0,0")));
        assertTrue(z2.getBoxes().contains(building.boxMap.get("-1,0")));
        assertTrue(z2.getBoxes().contains(building.boxMap.get("0,1")));

        // zone 3
        assertTrue(z3.getRooms().size() == 1);
        assertTrue(z3.getBoxes().size() == 3);
        assertTrue(z3.getBoxes().contains(building.boxMap.get("0,0")));
        assertTrue(z3.getBoxes().contains(building.boxMap.get("-1,0")));
        assertTrue(z3.getBoxes().contains(building.boxMap.get("0,-1")));

        // zone 4
        assertTrue(z4.getRooms().size() == 1);
        assertTrue(z4.getBoxes().size() == 3);
        assertTrue(z4.getBoxes().contains(building.boxMap.get("0,0")));
        assertTrue(z4.getBoxes().contains(building.boxMap.get("1,0")));
        assertTrue(z4.getBoxes().contains(building.boxMap.get("0,-1")));
    }
}
